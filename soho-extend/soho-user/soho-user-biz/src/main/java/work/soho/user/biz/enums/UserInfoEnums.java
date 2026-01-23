package work.soho.user.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class UserInfoEnums {
    /**
     * 状态
     */
    @RequiredArgsConstructor
    @Getter
    public enum Status {
        NORMAL(1,"正常"),
        DISABLED(0,"禁用");
        private final int id;
        private final String name;
    }
}
