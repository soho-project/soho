package work.soho.chat.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ChatCustomerServiceEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        OFFLINE(1,"下线"),
        ACTIVE(2,"活跃"),
        DISABLED(3,"禁用");
        private int id;
        private String name;

        Status(int i, String 下线) {
        }
    }
}
