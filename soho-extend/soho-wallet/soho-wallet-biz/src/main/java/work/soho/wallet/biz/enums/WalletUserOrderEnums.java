package work.soho.wallet.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class WalletUserOrderEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        PENDING_PAYMENT(0,"待支付"),
        PAYMENT_FAILED(40,"支付失败"),
        CANCELED(30,"已取消"),
        PAYMENT_SUCCESSFUL(20,"支付成功"),
        PROCESSING_PAYMENT(10,"支付中");
        private final int id;
        private final String name;
    }
}