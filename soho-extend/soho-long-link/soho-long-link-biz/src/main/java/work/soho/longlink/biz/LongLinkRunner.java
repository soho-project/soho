package work.soho.longlink.biz;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import work.soho.longlink.biz.websocket.WebSocketServer;

@Log4j2
@RequiredArgsConstructor
@Component
public class LongLinkRunner implements SmartLifecycle {
    private final WebSocketServer webSocketServer;

    @Value("${longlink.enable:true}")
    private Boolean enable;

    private volatile boolean running = false;

    @Override
    public void start() {
        if (!enable) {
            return;
        }
        log.info("---- longlink: starting ----");
        webSocketServer.start();
        running = true;
        log.info("---- longlink: started ----");
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }
        webSocketServer.stop();
        running = false;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }
}
