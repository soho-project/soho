package work.soho.chat.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ChatGroupUserEnums {
    /**
     * 是否是管理员
     */
    @RequiredArgsConstructor
    @Getter
    public enum IsAdmin {
        NO(0,"否"),
        YES(1,"是");
        private final int id;
        private final String name;
    }
}
