package work.soho.open.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OpenUserEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Type {
        INDIVIDUAL(1,"个人"),
        ENTERPRISE(2,"企业");
        private final int id;
        private final String name;
    }
}