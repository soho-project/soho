package work.soho.wallet.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class WalletRechargeEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        TO_BE_RECHARGED(0,"待充值"),
        RECHARGED(10,"已充值");
        private final int id;
        private final String name;
    }
}