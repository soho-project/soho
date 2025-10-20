package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ShopProductCategoryEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        DISABLE(0,"禁用"),
        ENABLE(1,"启用");
        private final int id;
        private final String name;
    }
}