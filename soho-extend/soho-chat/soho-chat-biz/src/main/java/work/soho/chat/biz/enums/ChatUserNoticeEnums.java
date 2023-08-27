package work.soho.chat.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ChatUserNoticeEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Type {
        FRIEND(1, "用户申请");

        private final int type;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        PENDING_PROCESSING(0,"待处理"),
        AGREE(1,"同意"),
        REFUSE(2,"拒绝"),
        PROCESSED(3,"已处理");
        private final int id;
        private final String name;
    }
}
