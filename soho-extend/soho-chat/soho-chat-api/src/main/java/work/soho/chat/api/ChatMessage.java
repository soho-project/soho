package work.soho.chat.api;

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

    public static ChatMessageBuilder builder() {
        return new ChatMessageBuilder();
    }

    public static class ChatMessageBuilder<T> {
        private String fromUid;
        private String toSessionId;
        private T message;

        public ChatMessageBuilder() {
        }

        public ChatMessageBuilder(T message, Long fromUid) {
            this(message, String.valueOf(fromUid));
        }
        public ChatMessageBuilder(T message, String fromUid) {
            this.fromUid = fromUid;
            this.message = message;
        }

        public ChatMessageBuilder(T message) {
            this(message, (Long) null);
        }

        public ChatMessageBuilder(Long toSessionId, T message) {
            this(String.valueOf(toSessionId), message, "0");
        }

        public ChatMessageBuilder(String toSessionId, T message) {
            this(toSessionId, message, "0");
        }

        public ChatMessageBuilder(String toSessionId, T message, String fromUid) {
            this.fromUid = fromUid;
            this.toSessionId = toSessionId;
            this.message = message;
        }

        /**
         * @param toSessionId
         * @param message
         * @param fromUid
         */
        public ChatMessageBuilder(Long toSessionId, T message, String fromUid) {
            this(String.valueOf(toSessionId), message, String.valueOf(fromUid));
        }

        public ChatMessageBuilder fromUid(String fromUid) {
            this.fromUid = fromUid;
            return this;
        }

        public ChatMessageBuilder fromUid(Long fromUid) {
            return fromUid(String.valueOf(fromUid));
        }

        public ChatMessageBuilder toSessionId(String toSessionId) {
            this.toSessionId = toSessionId;
            return this;
        }

        public ChatMessageBuilder toSessionId(Long toSessionId) {
            return toSessionId(String.valueOf(toSessionId));
        }

        public ChatMessageBuilder message(T message) {
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
