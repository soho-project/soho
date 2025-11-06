package work.soho.user.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class UserCertificationEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        TO_BE_CERTIFIED(0,"待认证"),
        PENDING(10, "待系统确认"),
        CERTIFIED(20,"已认证");
        private final int id;
        private final String name;
    }
}