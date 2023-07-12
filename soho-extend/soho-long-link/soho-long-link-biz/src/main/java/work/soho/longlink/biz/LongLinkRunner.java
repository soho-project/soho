package work.soho.longlink.biz;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import work.soho.longlink.biz.websocket.WebSocketServer;

@RequiredArgsConstructor
@Component
public class LongLinkRunner implements ApplicationRunner {
    private final WebSocketServer webSocketServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("######################################================================================================================================");
        (new Thread(webSocketServer)).start();
        System.out.println("runing long link");
        System.out.println("================================================================================================");
    }
}
