package work.soho.chat.api.payload;

import lombok.Data;

@Data
public class ChatMessage<T> {
    /**
     * 发送消息用户ID
     *
     * 0: 系统
     */
    private String fromUid = "0";

    /**
     * 接收会话ID
     */
    private String toSessionId;

    /**
     * 消息体
     */
    private T message;

    public static class Builder<T> {
        private String fromUid;
        private String toSessionId;
        private T message;

        public Builder(Long toSessionId, T message) {
            this(String.valueOf(toSessionId), message, "0");
        }

        public Builder(String toSessionId, T message) {
            this(toSessionId, message, "0");
        }


        public Builder(String toSessionId, T message, String fromUid) {
            this.fromUid = fromUid;
            this.toSessionId = toSessionId;
            this.message = message;
        }

        public Builder fromUid(String fromUid) {
            this.fromUid = fromUid;
            return this;
        }

        public Builder toSessionId(String toSessionId) {
            this.toSessionId = toSessionId;
            return this;
        }

        public Builder message(T message) {
            this.message = message;
            return this;
        }

        public ChatMessage build() {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setFromUid(fromUid);
            chatMessage.setMessage(message);
            chatMessage.setToSessionId(toSessionId);
            return chatMessage;
        }
    }
}
