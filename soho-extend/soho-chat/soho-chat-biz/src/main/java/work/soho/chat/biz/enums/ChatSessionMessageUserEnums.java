package work.soho.chat.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ChatSessionMessageUserEnums {

    /**
     * 消息是否已读
     */
    @Getter
    @RequiredArgsConstructor
    public enum IsRead {
        UNREAD(0, "未读"),
        READ(1, "已读")
        ;

        private final int id;
        private final String name;
    }
}
