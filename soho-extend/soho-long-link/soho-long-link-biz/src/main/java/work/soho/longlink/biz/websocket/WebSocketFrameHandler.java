package work.soho.longlink.biz.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import work.soho.longlink.api.chanel.MessageChanel;
import work.soho.longlink.biz.connect.ConnectManager;
import work.soho.longlink.biz.util.ServerUtil;

import java.net.InetSocketAddress;

/**
 * Echoes uppercase content of text frames.
 */
@RequiredArgsConstructor
@Log4j2
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final MessageChanel messageChanel;

    private final ConnectManager connectManager;

    private final static String OK = "+OK";
    private final static String ERR = "+ERR";
    private final static String PING = "ping";
    private final static String PONG = "pong";

    private String connectId = null;

    /**
     * 根据ctx获取连接ID
     *
     * @param ctx
     * @return
     */
    private String getConnectId(ChannelHandlerContext ctx) {
        if(connectId == null) {
            InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
            InetSocketAddress localIpSocket = (InetSocketAddress) ctx.channel().localAddress();
            if (ipSocket == null || localIpSocket == null) {
                return null;
            }
            connectId = ServerUtil.getConnectId(ipSocket, localIpSocket);
        }
        return connectId;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // ping and pong frames already handled
        if (frame instanceof TextWebSocketFrame) {
            // Send the uppercase string back.
            String request = ((TextWebSocketFrame) frame).text();
            if("ping".equals(request)) {
                ctx.channel().writeAndFlush(new TextWebSocketFrame(PONG));
            } else if ("pong".equals(request)) {
                //ignore
            } else {
                String userId = ctx.channel().attr(AuthHandshakeHandler.ATTR_USER_ID).get();
                messageChanel.onMessage(request, getConnectId(ctx), userId);
            }
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            //Channel upgrade to websocket, remove WebSocketIndexPageHandler.
            ctx.pipeline().remove(WebSocketIndexPageHandler.class);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel unregistered");
        String cid = getConnectId(ctx);
        String userId = ctx.channel().attr(AuthHandshakeHandler.ATTR_USER_ID).get();
        if (cid != null) {
            connectManager.removeConnect(cid);
        }
        if (cid != null && userId != null) {
            connectManager.removeConnectIdFromUid(cid, userId);
        }
        super.channelUnregistered(ctx);
    }
}
