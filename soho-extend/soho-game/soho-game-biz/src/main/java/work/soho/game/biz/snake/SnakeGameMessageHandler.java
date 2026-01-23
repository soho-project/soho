package work.soho.game.biz.snake;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import work.soho.longlink.api.handler.LongLinkMessageHandler;
import work.soho.longlink.api.message.LongLinkMessage;

@Log4j2
@Component
@RequiredArgsConstructor
public class SnakeGameMessageHandler implements LongLinkMessageHandler {
    /** 消息命名空间 */
    private static final String NAMESPACE = "soho-game";

    /** 游戏服务 */
    private final SnakeGameService snakeGameService;

    /**
     * 仅处理 soho-game 命名空间消息。
     */
    @Override
    public boolean supports(LongLinkMessage message) {
        return message != null && NAMESPACE.equalsIgnoreCase(message.getNamespace());
    }

    /**
     * 将消息交给游戏服务处理。
     */
    @Override
    public void onMessage(LongLinkMessage message, String connectId, String uid) {
        log.info("Game-snake----------收到消息： {}", message);
        snakeGameService.handleMessage(message, connectId, uid);
    }
}
