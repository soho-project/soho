package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ShopUserCouponsEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        UNUSED(0,"未使用"),
        USED(1,"已使用"),
        EXPIRED(2,"已过期");
        private final int id;
        private final String name;
    }
}