package work.soho.admin.biz.longlink;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import work.soho.admin.biz.service.AdminNotificationService;
import work.soho.common.core.util.JacksonUtils;
import work.soho.longlink.api.handler.LongLinkMessageHandler;
import work.soho.longlink.api.message.LongLinkMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class AdminNoticeMessageHandler implements LongLinkMessageHandler {
    private static final String NAMESPACE_ADMIN = "admin-notice";
    private static final String NAMESPACE_USER = "user-notice";
    private static final String RECEIVER_TYPE_ADMIN = "admin";
    private static final String RECEIVER_TYPE_USER = "user";
    private static final String TOPIC_READ = "read";

    private final AdminNotificationService adminNotificationService;

    @Override
    public boolean supports(LongLinkMessage message) {
        return message != null && (NAMESPACE_ADMIN.equals(message.getNamespace()) || NAMESPACE_USER.equals(message.getNamespace()));
    }

    @Override
    public void onMessage(LongLinkMessage message, String connectId, String uid) {
        String topic = message.getTopic();
        if (TOPIC_READ.equals(topic)) {
            handleRead(message.getPayload(), uid, resolveReceiverType(message.getNamespace()));
            return;
        }
        log.info("admin notice message ignored: topic={}, connectId={}, uid={}", topic, connectId, uid);
    }

    /**
     * 处理通知已读回执。
     */
    private void handleRead(Object payload, String uid, String receiverType) {
        Long receiverId = parseLong(uid);
        if (receiverId == null) {
            return;
        }
        List<Long> ids = extractIds(payload);
        if (ids.isEmpty()) {
            return;
        }
        boolean updated = adminNotificationService.readReceivers(receiverType, receiverId, ids);
        log.info("notice read updated: receiverType={}, receiverId={}, ids={}, updated={}", receiverType, receiverId, ids, updated);
    }

    /**
     * 根据命名空间解析接收角色。
     */
    private String resolveReceiverType(String namespace) {
        if (NAMESPACE_USER.equals(namespace)) {
            return RECEIVER_TYPE_USER;
        }
        return RECEIVER_TYPE_ADMIN;
    }

    private List<Long> extractIds(Object payload) {
        List<Long> ids = new ArrayList<>();
        if (payload == null) {
            return ids;
        }
        if (payload instanceof Number) {
            ids.add(((Number) payload).longValue());
            return ids;
        }
        if (payload instanceof Collection) {
            for (Object item : (Collection<?>) payload) {
                Long id = parseLong(item);
                if (id != null) {
                    ids.add(id);
                }
            }
            return ids;
        }
        if (payload instanceof Map) {
            return extractIdsFromMap((Map<?, ?>) payload);
        }
        if (payload instanceof String) {
            String text = ((String) payload).trim();
            if (text.isEmpty()) {
                return ids;
            }
            Map<Object, Object> map = JacksonUtils.toBean(text, Map.class);
            if (map != null && !map.isEmpty()) {
                return extractIdsFromMap(map);
            }
            if (text.contains(",")) {
                for (String item : text.split(",")) {
                    Long id = parseLong(item.trim());
                    if (id != null) {
                        ids.add(id);
                    }
                }
                return ids;
            }
            Long id = parseLong(text);
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    private List<Long> extractIdsFromMap(Map<?, ?> map) {
        List<Long> ids = new ArrayList<>();
        Object idValue = map.get("id");
        if (idValue != null) {
            Long id = parseLong(idValue);
            if (id != null) {
                ids.add(id);
                return ids;
            }
        }
        Object idsValue = map.get("ids");
        if (idsValue instanceof Collection) {
            for (Object item : (Collection<?>) idsValue) {
                Long id = parseLong(item);
                if (id != null) {
                    ids.add(id);
                }
            }
        } else if (idsValue != null) {
            Long id = parseLong(idsValue);
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    private Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            String text = String.valueOf(value).trim();
            if (text.isEmpty()) {
                return null;
            }
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
