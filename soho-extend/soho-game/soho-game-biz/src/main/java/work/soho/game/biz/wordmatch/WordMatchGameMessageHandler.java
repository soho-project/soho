package work.soho.game.biz.wordmatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import work.soho.longlink.api.handler.LongLinkMessageHandler;
import work.soho.longlink.api.message.LongLinkMessage;

/**
 * WordMatch 长连接消息处理器：路由到 WordMatchWsService。
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class WordMatchGameMessageHandler implements LongLinkMessageHandler {
    private static final String NAMESPACE = "soho-game";
    private static final String TOPIC = "word-match";

    private final WordMatchWsService wsService;

    @Override
    public boolean supports(LongLinkMessage message) {
        if (message == null) {
            return false;
        }
        String namespace = message.getNamespace();
        String topic = message.getTopic();
        return NAMESPACE.equalsIgnoreCase(namespace) && TOPIC.equalsIgnoreCase(topic);
    }

    @Override
    public void onMessage(LongLinkMessage message, String connectId, String uid) {
        log.info("Word-match message: {}", message);
        wsService.handleMessage(message, connectId, uid);
    }
}
