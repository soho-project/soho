package work.soho.longlink.biz.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import work.soho.longlink.api.authentication.Authentication;
import work.soho.longlink.api.chanel.MessageChanel;
import work.soho.longlink.biz.connect.ConnectManager;

import static work.soho.longlink.biz.util.ServerUtil.getConnectId;

@Component
@RequiredArgsConstructor
public class WebSocketServerInitializer  extends ChannelInitializer<SocketChannel> {

    private final Authentication authentication;

    private final MessageChanel messageChanel;

    private final ConnectManager connectManager;

    @Value("${longlink.path:/ws}")
    private String websocketPath;

    private SslContext sslCtx = null;

//    private final Authentication authentication;

//    public WebSocketServerInitializer(SslContext sslCtx) {
//        this.sslCtx = sslCtx;
//    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        // 可选：静态页/示例页 handler（不是必须）
        pipeline.addLast(new WebSocketIndexPageHandler(websocketPath));

        AuthHandshakeHandler.TokenAuthenticator authenticator = new AuthHandshakeHandler.TokenAuthenticator() {
            @Override
            public String authenticate(String token) throws Exception {
                return authentication.getUidWithToken(token);
            }

            @Override
            public void markAuthed(ChannelHandlerContext ctx, String userId) {
                connectManager.addConnect(ctx);
                connectManager.bindUid(getConnectId(ctx), userId);
            }
        };

        AuthHandshakeHandler.AuthOptions options = AuthHandshakeHandler.AuthOptions.defaults()
                .withFirstFrameTimeoutMillis(10_000);
        pipeline.addLast("wsAuthHttp", AuthHandshakeHandler.http(authenticator, websocketPath, options));


        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath, null, true));

        // 2) 第一帧鉴权（必须在 protocol 之后，业务 handler 之前）
        pipeline.addLast("wsAuthFirstFrame", AuthHandshakeHandler.firstFrame(authenticator, options));

        // 业务处理句柄
        pipeline.addLast(new WebSocketFrameHandler(messageChanel, connectManager));

    }
}
