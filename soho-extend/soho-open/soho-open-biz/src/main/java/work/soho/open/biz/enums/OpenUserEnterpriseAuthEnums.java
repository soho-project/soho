package work.soho.open.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OpenUserEnterpriseAuthEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        CONTINUATION(1,"存续"),
        LOG_OUT(2,"注销"),
        REVOKE(3,"吊销");
        private final int id;
        private final String name;
    }
}