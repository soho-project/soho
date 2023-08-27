package work.soho.chat.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ChatUserFriendApplyEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        PENDING_PROCESSING(0,"待处理"),
        AGREED(1,"已同意"),
        REJECTED(2,"已拒绝");

        private final int id;
        private final String name;
    }
}
