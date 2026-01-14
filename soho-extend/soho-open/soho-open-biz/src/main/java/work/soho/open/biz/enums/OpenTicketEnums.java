package work.soho.open.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OpenTicketEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        PENDING_REPLY(0,"待回复"),
        CLOSED(20,"已关闭"),
        REPLIED(10,"已回复");
        private final int id;
        private final String name;
    }
}