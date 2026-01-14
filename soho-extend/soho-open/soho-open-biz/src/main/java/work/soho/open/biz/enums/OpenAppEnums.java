package work.soho.open.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OpenAppEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        DISABLED(0,"禁用"),
        ACTIVE(1,"活跃");
        private final int id;
        private final String name;
    }
}