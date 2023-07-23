package work.soho.chat.api.payload;

import lombok.Data;

@Data
public class ChatMessage<T> {
    /**
     * 发送消息用户ID
     */
    private String fromUid;

    /**
     * 接收会话ID
     */
    private String toSessionId;

    /**
     * 消息体
     */
    private T message;
}
