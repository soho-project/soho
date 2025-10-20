package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ShopUserAddressesEnums {

    @RequiredArgsConstructor
    @Getter
    public enum IsDeleted {
        NO(0,"否"),
        YES(1,"是");
        private final int id;
        private final String name;
    }
}