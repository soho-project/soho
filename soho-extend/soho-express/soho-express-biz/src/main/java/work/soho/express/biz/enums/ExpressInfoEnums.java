package work.soho.express.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ExpressInfoEnums {

    @RequiredArgsConstructor
    @Getter
    public enum ExpressType {
        ZTO_EXPRESS(1,"中通快递"),
        YTO_EXPRESS(2,"韵达快递");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        DISABLE(0,"禁用"),
        ACTIVE(1,"活跃");
        private final int id;
        private final String name;
    }
}