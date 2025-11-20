package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

public class ShopCouponUsageLogsEnums {
    @Getter
    @RequiredArgsConstructor
    public enum ScopeType {
        CATEGORY(1, "分类"),
        PRODUCT(2, "商品");

        private final int id;
        private final String name;

        public static ScopeType fromId(int id) {
            return Arrays.stream(values())
                    .filter(type -> type.id == id)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("未知的范围类型: " + id));
        }
    }
}