package work.soho.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WalletTypeNameEnums {

    RMB("rmb", "人民币"),
    RY("ry", "茸元"),
    ;

    private final String name;
    private final String desc;
}
