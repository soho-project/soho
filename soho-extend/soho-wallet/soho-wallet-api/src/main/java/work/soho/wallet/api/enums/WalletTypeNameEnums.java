package work.soho.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WalletTypeNameEnums {

    RMB(1, "rmb", "人民币"),
    POINT(2, "point", "茸元"),
    ;

    private final int id;
    private final String name;
    private final String desc;
}
