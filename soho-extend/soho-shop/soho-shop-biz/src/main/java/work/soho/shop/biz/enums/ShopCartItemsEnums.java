package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ShopCartItemsEnums {

    @RequiredArgsConstructor
    @Getter
    public enum IsSelected {
        NOT_DELETED(0,"未删除"),
        DELETED(1,"已删除");
        private final int id;
        private final String name;
    }
}