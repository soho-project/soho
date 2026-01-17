package work.soho.longlink.biz.message;

import org.springframework.stereotype.Component;
import work.soho.common.core.util.JacksonUtils;
import work.soho.longlink.api.message.LongLinkMessage;

import java.util.Map;

@Component
public class LongLinkMessageParser {

    public LongLinkMessage parse(String raw) {
        String text = raw == null ? "" : raw;
        LongLinkMessage message = tryParseEnvelope(text);
        if (message == null) {
            message = new LongLinkMessage();
            message.setPayload(text);
        }
        applyDefaults(message, text);
        return message;
    }

    private LongLinkMessage tryParseEnvelope(String raw) {
        String text = raw == null ? "" : raw.trim();
        if (!(text.startsWith("{") && text.endsWith("}"))) {
            return null;
        }
        Map<String, Object> map = JacksonUtils.toBean(text, Map.class);
        if (map == null || map.isEmpty()) {
            return null;
        }
        if (!(map.containsKey("namespace")
                || map.containsKey("topic")
                || map.containsKey("type")
                || map.containsKey("payload")
                || map.containsKey("traceId")
                || map.containsKey("headers"))) {
            return null;
        }
        return JacksonUtils.toBean(text, LongLinkMessage.class);
    }

    private void applyDefaults(LongLinkMessage message, String raw) {
        if (message.getNamespace() == null || message.getNamespace().isBlank()) {
            message.setNamespace(LongLinkMessage.DEFAULT_NAMESPACE);
        }
        if (message.getTopic() == null || message.getTopic().isBlank()) {
            message.setTopic(LongLinkMessage.DEFAULT_TOPIC);
        }
        if (message.getType() == null || message.getType().isBlank()) {
            message.setType(LongLinkMessage.DEFAULT_TYPE);
        }
        if (message.getPayload() == null) {
            message.setPayload(raw == null ? "" : raw);
        }
    }
}
