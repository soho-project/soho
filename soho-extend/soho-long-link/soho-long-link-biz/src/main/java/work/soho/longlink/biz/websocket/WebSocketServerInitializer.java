package work.soho.longlink.biz.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import work.soho.longlink.api.authentication.Authentication;
import work.soho.longlink.api.chanel.MessageChanel;

@Component
@RequiredArgsConstructor
public class WebSocketServerInitializer  extends ChannelInitializer<SocketChannel> {

    private static final String WEBSOCKET_PATH = "/ws";

    private final Authentication authentication;

    private final MessageChanel messageChanel;

    private SslContext sslCtx = null;

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
        pipeline.addLast(new WebSocketIndexPageHandler(WEBSOCKET_PATH));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
        pipeline.addLast(new WebSocketFrameHandler(authentication, messageChanel));
    }
}
