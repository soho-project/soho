package work.soho.pay.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class PayHfpayWalletEnums {
    @Getter
    @RequiredArgsConstructor
    public enum Status {
        /**
         * 状态
         */
        CREATE(0, "待创建"),
        SUCCESS(1, "已创建"),
        FAIL(2, "取消"),
        ;

        private final Integer code;
        private final String desc;
    }
}