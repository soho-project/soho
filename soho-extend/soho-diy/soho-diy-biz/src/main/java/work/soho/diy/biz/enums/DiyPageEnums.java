package work.soho.diy.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class DiyPageEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        DISABLE(0,"禁用"),
        ACTIVE(1,"活跃");
        private final int id;
        private final String name;
    }
}