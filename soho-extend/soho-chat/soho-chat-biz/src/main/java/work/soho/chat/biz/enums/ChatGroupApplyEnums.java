package work.soho.chat.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ChatGroupApplyEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        PENDING_PROCESSING(0,"待处理"),
        AGREE(1,"同意"),
        REFUSE(2,"拒绝");
        private final int id;
        private final String name;
    }

}
