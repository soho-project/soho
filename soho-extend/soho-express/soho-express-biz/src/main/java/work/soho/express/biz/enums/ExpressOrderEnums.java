package work.soho.express.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ExpressOrderEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        PENDING(0,"待处理"),
        SENT(1,"已发送"),
        INTERCEPT_PENDING(20, "拦截中"),
        INTERCEPT_SUCCESS(21, "拦截成功"),
        CANCEL(50, "已取消"),
        SUCCESS(60, "已完成");
        private final int id;
        private final String name;
    }
}