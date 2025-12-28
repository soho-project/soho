package work.soho.express.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ExpressOrderEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        PENDING(0,"待处理"),
        SENT(1,"已发送");
        private final int id;
        private final String name;
    }
}