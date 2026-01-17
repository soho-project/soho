package work.soho.longlink.biz.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import work.soho.longlink.api.chanel.MessageChanel;
import work.soho.longlink.api.event.MessageEvent;
import work.soho.longlink.api.message.LongLinkMessage;
import work.soho.longlink.biz.router.MessageRouter;

@Log4j2
@Component
@RequiredArgsConstructor
public class MessageChannelImpl implements MessageChanel {
    private final ApplicationContext applicationContext;
    private final LongLinkMessageParser messageParser;
    private final MessageRouter messageRouter;

    @Override
    public void onMessage(String message) {
        onMessage(message, null, null);
    }

    @Override
    public void onMessage(String message, String connectId, String uid) {
        LongLinkMessage parsed = messageParser.parse(message);
        messageRouter.route(parsed, connectId, uid);
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setPayload(message);
        messageEvent.setConnectId(connectId);
        messageEvent.setUid(uid);
        messageEvent.setMessage(parsed);
        applicationContext.publishEvent(messageEvent);
        log.info("收到消息： {}", message);
        log.info("publish event: {}", messageEvent);
    }
}
