package work.soho.chat.api.request;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.LinkedList;

public class AiRequest {

    @RequiredArgsConstructor
    @Getter
    public enum Role {
        USER(1, "user"),
        ASSISTANT(2, "assistant"),
        SYSTEM(3, "system");

        private final int id;
        private final String name;
    }

    @Data
    public static class Message{
        private String role;
        private String content;
    }

    public static class BuildMessages {
        private LinkedList<Message> list = new LinkedList<>();

        /**
         * 追加用户消息
         *
         * @param msg
         * @return
         */
        public BuildMessages userMessage(String msg) {
            Message message = new Message();
            message.setRole(Role.USER.name);
            message.setContent(msg);
            list.add(message);
            return this;
        }

        public BuildMessages systemMessage(String msg) {
            Message message = new Message();
            message.setRole(Role.SYSTEM.name);
            message.setContent(msg);
            list.add(message);
            return this;
        }

        public BuildMessages assistantMessage(String msg) {
            Message message = new Message();
            message.setRole(Role.USER.name);
            message.setContent(msg);
            list.add(message);
            return this;
        }

        public LinkedList<Message> build() {
            return list;
        }
    }
}
