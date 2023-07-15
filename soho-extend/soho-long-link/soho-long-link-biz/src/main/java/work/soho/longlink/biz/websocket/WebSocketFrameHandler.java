package work.soho.longlink.biz.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.RequiredArgsConstructor;
import work.soho.longlink.api.authentication.Authentication;
import work.soho.longlink.api.chanel.MessageChanel;
import work.soho.longlink.biz.connect.ConnectManager;
import work.soho.longlink.biz.util.ServerUtil;

import java.net.InetSocketAddress;
import java.util.Locale;

/**
 * Echoes uppercase content of text frames.
 */
@RequiredArgsConstructor
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final Authentication authentication;
    private final MessageChanel messageChanel;

    private final ConnectManager connectManager;

    private final static String OK = "+OK";
    private final static String ERR = "+ERR";

    private String connectId = null;

    /**
     * 认证通过的UID
     */
    private String uid = null;

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
            //判断是否已经鉴权
            if(uid == null) {
                uid = authentication.getUidWithToken(request);
                if(uid == null) {
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(ERR));
                } else {
                    connectManager.addConnect(ctx);
                    connectManager.bindUid(getConnectId(ctx), uid);
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(OK));
                }
            } else {
//                ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));
                messageChanel.onMessage(request, getConnectId(ctx), uid);
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
        System.out.println("通道卸载");
        connectManager.removeConnect(getConnectId(ctx));
        connectManager.removeConnectIdFromUid(getConnectId(ctx), uid);
        super.channelUnregistered(ctx);
    }
}
