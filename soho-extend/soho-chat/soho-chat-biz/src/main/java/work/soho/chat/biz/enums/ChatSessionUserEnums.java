package work.soho.chat.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ChatSessionUserEnums {
    /**
     * 会话用户状态
     */
    @Getter
    @RequiredArgsConstructor
    public enum Status {
        DELETED(0, "已删除"),
        ACTIVE(1, "活跃")
        ;

        private final int id;
        private final String name;
    }

    @Getter
    @RequiredArgsConstructor
    public enum IsShield {
        NO(0,"否"),
        YES(1, "是");

        private final int id;
        private final String name;
    }
}
