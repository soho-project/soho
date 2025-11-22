package work.soho.wallet.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class WalletTypeEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        DISABLE(0,"禁用"),
        ACTIVE(1,"活跃");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum CanWithdrawal {
        CANNOT(0,"不能"),
        CAN(1,"能");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum CanRecharge {
        NO(0,"否"),
        CAN(1,"能");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum CanTransferOut {
        NO(0,"否"),
        YES(1,"是");
        private final int id;
        private final String name;
    }
}