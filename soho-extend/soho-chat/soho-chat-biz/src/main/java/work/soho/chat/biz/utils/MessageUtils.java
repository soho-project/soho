package work.soho.chat.biz.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.experimental.UtilityClass;
import work.soho.chat.api.ChatMessage;
import work.soho.chat.api.payload.*;
import work.soho.common.core.util.JacksonUtils;
import work.soho.longlink.api.event.MessageEvent;

import java.util.Map;

/**
 * 消息解析工具类
 */
@UtilityClass
public class MessageUtils {

    /**
     * 解析客户端消息
     *
     * @param jsonString
     * @return
     */
    public ChatMessage fromString(String jsonString) {
        Map<Object, Object> map = JacksonUtils.toBean(jsonString, Map.class);
        System.out.println( map);
        ChatMessage upMessage = null;
        switch ((String) ((Map)map.get("message")).get("type")) {
            case "text":
                upMessage = JacksonUtils.toBean(jsonString, new TypeReference<ChatMessage<Text>>() {});
                break;
            case "image":
                upMessage = JacksonUtils.toBean(jsonString, new TypeReference<ChatMessage<Image>>() {});
                break;
            case "file":
                upMessage = JacksonUtils.toBean(jsonString, new TypeReference<ChatMessage<File>>() {});
                break;
            case "videoPhone":
                upMessage = JacksonUtils.toBean(jsonString, new TypeReference<ChatMessage<VideoPhone>>() {});
                break;
            case "command":
                upMessage = JacksonUtils.toBean(jsonString, new TypeReference<ChatMessage<Command>>() {});
                break;
            default:
                return null;
        }
        return upMessage;
    }

    /**
     * 从MessageEvent解析会话消息
     *
     * @param messageEvent
     * @return
     */
    public ChatMessage fromMessageEvent(MessageEvent messageEvent) {
        ChatMessage chatMessage = fromString(messageEvent.getPayload());
        chatMessage.setFromUid(messageEvent.getUid());
        return chatMessage;
    }
}
