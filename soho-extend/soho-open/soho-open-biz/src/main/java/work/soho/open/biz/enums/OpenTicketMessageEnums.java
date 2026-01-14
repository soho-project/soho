package work.soho.open.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OpenTicketMessageEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Type {
        USER_MESSAGE(0,"用户消息"),
        ADMINISTRATOR_MESSAGE(1,"管理员消息");
        private final int id;
        private final String name;
    }
}