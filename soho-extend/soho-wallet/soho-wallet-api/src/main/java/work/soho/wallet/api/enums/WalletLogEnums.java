package work.soho.wallet.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class WalletLogEnums {
    @RequiredArgsConstructor
    @Getter
    public enum BizId {
        PERFORMANCE_SHARING(1,"业绩分成"),
        MEDAL_DISCHARGE(2,"勋章发放"),
        MALL_PAYMENT(3, "商城支付"),
        MALL_REFUNDED(4, "商城退款"),
        WALLET_TRANSFER_OUT(5, "账户转账出账"),
        WALLET_TRANSFER_IN(5, "账户转账入账"),
        PAY_ORDER(6, "支付单"),
        ;

        private final int id;
        private final String name;
    }
}