package work.soho.ai.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class AiAppEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        DISABLED(0,"禁用"),
        ENABLE(1,"启用");
        private final int id;
        private final String name;
    }
}