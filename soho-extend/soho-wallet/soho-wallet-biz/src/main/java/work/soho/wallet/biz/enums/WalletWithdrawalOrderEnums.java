package work.soho.wallet.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class WalletWithdrawalOrderEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        PENDING_PROCESSING(0,"待处理"),
        WITHDRAWN(1,"已提现"),
        REJECTED(2,"已拒绝");
        private final int id;
        private final String name;
    }
}