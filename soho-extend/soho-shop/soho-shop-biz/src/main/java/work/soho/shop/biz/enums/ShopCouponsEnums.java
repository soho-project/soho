package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ShopCouponsEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Type {
        FULL_REDUCTION(1,"满额减"),
        FULL_DISCOUNT(2,"满额折扣"),
        FREE_SHIPPING(3,"免运费");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        DISABLE(0,"禁用"),
        ENABLE(1,"启用");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum ApplyScope {
        ALL_PLATFORMS(1,"全平台"),
        WHOLE_STORE(2,"全店铺"),
        SPECIFY_CLASSIFICATION(3,"指定分类"),
        SPECIFIED_GOODS(4,"指定商品");
        private final int id;
        private final String name;
    }
}