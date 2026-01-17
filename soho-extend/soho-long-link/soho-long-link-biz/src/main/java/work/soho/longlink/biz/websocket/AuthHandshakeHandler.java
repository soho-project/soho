package work.soho.longlink.biz.websocket;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderValues.UPGRADE;

/**
 * WebSocket 鉴权（握手阶段 + 第一帧）完整实现：
 *  - header token（Authorization: Bearer xxx / X-Token 等）
 *  - cookie token（token/access_token 等）
 *  - url query token（?token=xxx / ?access_token=xxx）
 *  - 第一帧 token（纯文本 或 JSON {"token":"..."} / {"access_token":"..."}）
 *
 * ✅ 关键修复：不再在一个方法里同时 add “HTTP阶段handler + WS阶段handler”，避免顺序错乱。
 * 你应该在 pipeline 中：
 *   - WebSocketServerProtocolHandler 之前 add AuthHandshakeHandler.http(...)
 *   - WebSocketServerProtocolHandler 之后 add AuthHandshakeHandler.firstFrame(...)
 */
public final class AuthHandshakeHandler {

    public interface TokenAuthenticator {
        /**
         * @return 认证成功返回 userId（或任意主体标识），失败返回 null
         */
        String authenticate(String token) throws Exception;

        /**
         * 标记通道已认证
         *
         * @param ctx
         * @param userId
         */
        void markAuthed(ChannelHandlerContext ctx, String userId);
    }

    public static final AttributeKey<String> ATTR_USER_ID = AttributeKey.valueOf("ws.userId");
    public static final AttributeKey<Boolean> ATTR_AUTHED = AttributeKey.valueOf("ws.authed");

    private AuthHandshakeHandler() {}

    // -------------------- Options --------------------

    public static final class AuthOptions {
        /** 会依次尝试这些 header 获取 token */
        public final List<String> tokenHeaderNames;
        /** 会依次尝试这些 cookie 获取 token */
        public final List<String> tokenCookieNames;
        /** 会依次尝试这些 query 参数获取 token */
        public final List<String> tokenQueryNames;

        /** 是否接受 Authorization: Bearer xxx */
        public final boolean acceptBearer;

        /** HTTP 阶段没拿到 token 时，是否允许升级后等第一帧 token */
        public final boolean allowFirstFrameAuth;
        /** 等第一帧 token 的超时，超时会断开（防止占连接） */
        public final long firstFrameTimeoutMillis;

        /** 第一帧支持纯 token 文本（不是 JSON 时当 token） */
        public final boolean firstFrameAcceptPlainToken;
        /** 第一帧支持 JSON {"token":"..."} 或 {"access_token":"..."} */
        public final boolean firstFrameAcceptJsonToken;

        private AuthOptions(
                List<String> tokenHeaderNames,
                List<String> tokenCookieNames,
                List<String> tokenQueryNames,
                boolean acceptBearer,
                boolean allowFirstFrameAuth,
                long firstFrameTimeoutMillis,
                boolean firstFrameAcceptPlainToken,
                boolean firstFrameAcceptJsonToken
        ) {
            this.tokenHeaderNames = tokenHeaderNames;
            this.tokenCookieNames = tokenCookieNames;
            this.tokenQueryNames = tokenQueryNames;
            this.acceptBearer = acceptBearer;
            this.allowFirstFrameAuth = allowFirstFrameAuth;
            this.firstFrameTimeoutMillis = firstFrameTimeoutMillis;
            this.firstFrameAcceptPlainToken = firstFrameAcceptPlainToken;
            this.firstFrameAcceptJsonToken = firstFrameAcceptJsonToken;
        }

        public static AuthOptions defaults() {
            return new AuthOptions(
                    Arrays.asList("Authorization", "X-Token", "X-Access-Token"),
                    Arrays.asList("token", "access_token", "Authorization"),
                    Arrays.asList("token", "access_token"),
                    true,
                    true,
                    10_000,
                    true,
                    true
            );
        }

        public AuthOptions withAllowFirstFrameAuth(boolean allow) {
            return new AuthOptions(
                    this.tokenHeaderNames, this.tokenCookieNames, this.tokenQueryNames,
                    this.acceptBearer,
                    allow,
                    this.firstFrameTimeoutMillis,
                    this.firstFrameAcceptPlainToken,
                    this.firstFrameAcceptJsonToken
            );
        }

        public AuthOptions withFirstFrameTimeoutMillis(long ms) {
            return new AuthOptions(
                    this.tokenHeaderNames, this.tokenCookieNames, this.tokenQueryNames,
                    this.acceptBearer,
                    this.allowFirstFrameAuth,
                    ms,
                    this.firstFrameAcceptPlainToken,
                    this.firstFrameAcceptJsonToken
            );
        }
    }

    // -------------------- Factory methods (FIXED) --------------------

    /** 放在 WebSocketServerProtocolHandler 之前 */
    public static ChannelHandler http(TokenAuthenticator authenticator, String websocketPath, AuthOptions options) {
        return new HttpAuthHandler(authenticator, websocketPath, options == null ? AuthOptions.defaults() : options);
    }

    /** 放在 WebSocketServerProtocolHandler 之后（在你的业务 WebSocketFrameHandler 之前） */
    public static ChannelHandler firstFrame(TokenAuthenticator authenticator, AuthOptions options) {
        return new FirstFrameAuthHandler(authenticator, options == null ? AuthOptions.defaults() : options);
    }

    // -------------------- HTTP stage handler --------------------

    private static final class HttpAuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        private final TokenAuthenticator authenticator;
        private final String websocketPath;
        private final AuthOptions options;

        HttpAuthHandler(TokenAuthenticator authenticator, String websocketPath, AuthOptions options) {
            super(false);
            this.authenticator = Objects.requireNonNull(authenticator, "authenticator");
            this.websocketPath = Objects.requireNonNull(websocketPath, "websocketPath");
            this.options = Objects.requireNonNull(options, "options");
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
            // 只对 websocket upgrade 请求做鉴权；普通静态页面/HTTP 直接放行给后续 HTTP handler
            if (!isWebSocketUpgradeRequest(req, websocketPath)) {
                ctx.fireChannelRead(req.retain());
                return;
            }

            // 已认证就放行
            if (Boolean.TRUE.equals(ctx.channel().attr(ATTR_AUTHED).get())) {
                ctx.fireChannelRead(req.retain());
                return;
            }

            String token = extractTokenFromHttp(req, options);

            if (token != null && !token.isBlank()) {
                String userId = safeAuth(authenticator, token);
                if (userId != null) {
                    markAuthed(authenticator, ctx, userId);
                    ctx.fireChannelRead(req.retain());
                    return;
                }
                // token 有但无效：拒绝握手更安全
                sendHttpErrorAndClose(ctx, req, HttpResponseStatus.UNAUTHORIZED, "Invalid token");
                return;
            }

            // 没 token：允许第一帧鉴权 or 直接拒绝
            if (options.allowFirstFrameAuth) {
                ctx.fireChannelRead(req.retain());
            } else {
                sendHttpErrorAndClose(ctx, req, HttpResponseStatus.UNAUTHORIZED, "Missing token");
            }
        }

        private static boolean isWebSocketUpgradeRequest(FullHttpRequest req, String websocketPath) {
            String uri = req.uri();
            String path;
            try {
                path = new URI(uri).getPath();
            } catch (Exception ignore) {
                int q = uri.indexOf('?');
                path = (q >= 0 ? uri.substring(0, q) : uri);
            }
            if (!Objects.equals(path, websocketPath)) return false;

            String upgrade = req.headers().get(UPGRADE);
            return upgrade != null && "websocket".equalsIgnoreCase(upgrade);
        }

        private static void sendHttpErrorAndClose(ChannelHandlerContext ctx, FullHttpRequest req,
                                                  HttpResponseStatus status, String msg) {
            byte[] bytes = msg.getBytes(CharsetUtil.UTF_8);
            FullHttpResponse resp = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, status, Unpooled.wrappedBuffer(bytes)
            );
            resp.headers().set(CONTENT_TYPE, "text/plain; charset=utf-8");
            resp.headers().setInt(CONTENT_LENGTH, bytes.length);

            boolean keepAlive = HttpUtil.isKeepAlive(req);
            if (keepAlive) {
                resp.headers().set(CONNECTION, KEEP_ALIVE);
                ctx.writeAndFlush(resp);
            } else {
                ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    // -------------------- WS stage handler (first frame auth) --------------------

    private static final class FirstFrameAuthHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
        private final TokenAuthenticator authenticator;
        private final AuthOptions options;

        FirstFrameAuthHandler(TokenAuthenticator authenticator, AuthOptions options) {
            super(false);
            this.authenticator = Objects.requireNonNull(authenticator, "authenticator");
            this.options = Objects.requireNonNull(options, "options");
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            if (!options.allowFirstFrameAuth) return;

            // 等第一帧 token 超时：直接断开（避免占用连接）
            ctx.channel().eventLoop().schedule(() -> {
                Channel ch = ctx.channel();
                if (!ch.isActive()) return;
                if (!Boolean.TRUE.equals(ch.attr(ATTR_AUTHED).get())) {
                    // 更标准：1008 Policy Violation
                    ch.writeAndFlush(new CloseWebSocketFrame(1008, "Missing auth token"))
                            .addListener(ChannelFutureListener.CLOSE);
                }
            }, options.firstFrameTimeoutMillis, TimeUnit.MILLISECONDS);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
            if (Boolean.TRUE.equals(ctx.channel().attr(ATTR_AUTHED).get())) {
                ctx.fireChannelRead(frame.retain());
                return;
            }

            // 未认证：只接受 Text 帧作为认证帧
            if (frame instanceof TextWebSocketFrame) {
                String text = ((TextWebSocketFrame) frame).text();
                String token = extractTokenFromFirstFrame(text, options);

                if (token != null && !token.isBlank()) {
                    String userId = safeAuth(authenticator, token);
                    if (userId != null) {
                        markAuthed(authenticator, ctx, userId);

                        // 可选：给客户端回一个认证成功消息（你也可以删掉）
//                        ctx.writeAndFlush(new TextWebSocketFrame("{\"type\":\"auth_ok\"}"));
                        ctx.writeAndFlush(new TextWebSocketFrame("+OK"));

                        // 认证帧不再交给业务层（避免业务误处理）
                        return;
                    }
                }

                // token 无效：关闭（1008 更语义化）
                ctx.writeAndFlush(new CloseWebSocketFrame(1008, "Invalid auth token"))
                        .addListener(ChannelFutureListener.CLOSE);
                return;
            }

            // 其他帧但未认证：关闭
            ctx.writeAndFlush(new CloseWebSocketFrame(1008, "Unauthenticated"))
                    .addListener(ChannelFutureListener.CLOSE);
        }
    }

    // -------------------- Helpers --------------------

    private static void markAuthed(TokenAuthenticator authenticator, ChannelHandlerContext ctx, String userId) {
        ctx.channel().attr(ATTR_USER_ID).set(userId);
        ctx.channel().attr(ATTR_AUTHED).set(true);
        authenticator.markAuthed(ctx, userId);
    }

    private static String safeAuth(TokenAuthenticator authenticator, String token) {
        try {
            return authenticator.authenticate(token);
        } catch (Exception e) {
            return null;
        }
    }

    private static String extractTokenFromHttp(FullHttpRequest req, AuthOptions options) {
        // 1) Header
        String headerToken = extractFromHeaders(req.headers(), options);
        if (headerToken != null) return headerToken;

        // 2) Cookie
        String cookieToken = extractFromCookies(req.headers(), options);
        if (cookieToken != null) return cookieToken;

        // 3) Query
        String queryToken = extractFromQuery(req.uri(), options);
        if (queryToken != null) return queryToken;

        return null;
    }

    private static String extractFromHeaders(HttpHeaders headers, AuthOptions options) {
        if (options.acceptBearer) {
            String auth = headers.get("Authorization");
            if (auth != null) {
                String v = auth.trim();
                if (v.regionMatches(true, 0, "Bearer ", 0, 7)) {
                    String token = v.substring(7).trim();
                    if (!token.isEmpty()) return token;
                }
            }
        }

        for (String name : options.tokenHeaderNames) {
            String v = headers.get(name);
            if (v == null) continue;
            v = v.trim();
            if (v.isEmpty()) continue;

            // Authorization 非 Bearer 情况下兜底直接返回
            return v;
        }
        return null;
    }

    private static String extractFromCookies(HttpHeaders headers, AuthOptions options) {
        String cookieHeader = headers.get("Cookie");
        if (cookieHeader == null || cookieHeader.isBlank()) return null;

        Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieHeader);
        if (cookies == null || cookies.isEmpty()) return null;

        for (String cookieName : options.tokenCookieNames) {
            for (Cookie c : cookies) {
                if (cookieName.equalsIgnoreCase(c.name())) {
                    String v = c.value();
                    if (v != null && !v.isBlank()) return v.trim();
                }
            }
        }
        return null;
    }

    private static String extractFromQuery(String uri, AuthOptions options) {
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Map<String, List<String>> params = decoder.parameters();
        if (params == null || params.isEmpty()) return null;

        for (String key : options.tokenQueryNames) {
            List<String> vs = params.get(key);
            if (vs == null || vs.isEmpty()) continue;
            String v = vs.get(0);
            if (v != null && !v.isBlank()) return v.trim();
        }
        return null;
    }

    private static String extractTokenFromFirstFrame(String text, AuthOptions options) {
        if (text == null) return null;
        String s = text.trim();
        if (s.isEmpty()) return null;

        // 1) 非 JSON：当作纯 token
        if (options.firstFrameAcceptPlainToken) {
            if (!(s.startsWith("{") && s.endsWith("}"))) {
                return s;
            }
        }

        // 2) JSON：{"token":"..."} 或 {"access_token":"..."}
        if (options.firstFrameAcceptJsonToken) {
            if (s.startsWith("{") && s.endsWith("}")) {
                String v = findJsonStringValue(s, "token");
                if (v != null) return v;
                v = findJsonStringValue(s, "access_token");
                if (v != null) return v;
            }
        }

        return null;
    }

    private static String findJsonStringValue(String json, String key) {
        // 轻量解析：查找 "key" : "value"
        // 生产更建议直接用 Jackson 解析第一帧 JSON，这里为了“零依赖”给出最小实现
        String k = "\"" + key + "\"";
        int i = json.indexOf(k);
        if (i < 0) return null;

        int colon = json.indexOf(':', i + k.length());
        if (colon < 0) return null;

        int p = colon + 1;
        while (p < json.length() && Character.isWhitespace(json.charAt(p))) p++;
        if (p >= json.length() || json.charAt(p) != '"') return null;

        int start = p + 1;
        int end = start;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == '"' && json.charAt(end - 1) != '\\') break;
            end++;
        }
        if (end >= json.length()) return null;

        String raw = json.substring(start, end);
        return raw.replace("\\\"", "\"").trim();
    }
}
