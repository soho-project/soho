package work.soho.longlink.biz.router;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import work.soho.longlink.api.handler.LongLinkMessageHandler;
import work.soho.longlink.api.message.LongLinkMessage;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class DefaultMessageRouter implements MessageRouter {
    private final List<LongLinkMessageHandler> handlers;

    @Override
    public void route(LongLinkMessage message, String connectId, String uid) {
        if (handlers == null || handlers.isEmpty()) {
            return;
        }
        for (LongLinkMessageHandler handler : handlers) {
            if (handler == null) {
                continue;
            }
            try {
                if (handler.supports(message)) {
                    handler.onMessage(message, connectId, uid);
                }
            } catch (Exception e) {
                log.warn("longlink handler error: {}", handler.getClass().getName(), e);
            }
        }
    }
}
