package work.soho.longlink.biz.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import work.soho.longlink.biz.util.ServerUtil;

/**
 * An HTTP server which serves Web Socket requests at:
 *
 * http://localhost:8080/websocket
 *
 * Open your browser at <a href="http://localhost:8080/">http://localhost:8080/</a>, then the demo page will be loaded
 * and a Web Socket connection will be made automatically.
 *
 * This server illustrates support for the different web socket specification versions and will work with:
 *
 * <ul>
 * <li>Safari 5+ (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 6-13 (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 14+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>Chrome 16+ (RFC 6455 aka draft-ietf-hybi-thewebsocketprotocol-17)
 * <li>Firefox 7+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>Firefox 11+ (RFC 6455 aka draft-ietf-hybi-thewebsocketprotocol-17)
 * </ul>
 */
@Log4j2
@Component
@RequiredArgsConstructor
public final class WebSocketServer {

    static final boolean SSL = false;

    private final WebSocketServerInitializer webSocketServerInitializer;

    @Value("${longlink.port:8080}")
    private int port;

    private volatile boolean running = false;
    private volatile EventLoopGroup bossGroup;
    private volatile EventLoopGroup workerGroup;
    private volatile Channel channel;
    private volatile Thread serverThread;

    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        serverThread = new Thread(() -> {
            try {
                startWebsocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "longlink-websocket");
        serverThread.start();
    }

    public synchronized void stop() {
        if (!running) {
            return;
        }
        running = false;
        if (channel != null) {
            channel.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    public boolean isRunning() {
        return running;
    }

    private void startWebsocket() throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(webSocketServerInitializer);

            channel = b.bind(port).sync().channel();

            log.info("longlink websocket: {}://127.0.0.1:{}/",
                    (SSL ? "https" : "http"), port);

            channel.closeFuture().sync();
        } finally {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        }
    }
}
