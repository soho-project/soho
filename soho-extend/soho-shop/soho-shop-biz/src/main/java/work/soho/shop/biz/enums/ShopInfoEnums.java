package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ShopInfoEnums {
    @RequiredArgsConstructor
    @Getter
    public enum Status {
        PENDING_REVIEW(0,"待审核"),
        ACTIVE(30,"活跃"),
        DISABLE(20,"禁用"),
        UNDER_REVIEW(10,"审核中");
        private final int id;
        private final String name;
    }
}