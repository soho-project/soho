package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ShopCouponApplyRangesEnums {

    @RequiredArgsConstructor
    @Getter
    public enum ScopeType {
        PRODUCT_CATEGORY(1,"商品分类"),
        COMMODITY(2,"商品");
        private final int id;
        private final String name;
    }
}