package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ShopProductInfoEnums {

    @RequiredArgsConstructor
    @Getter
    public enum ShelfStatus {
        TAKE_DOWN(0,"下架"),
        LAUNCH(1,"上架");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum AuditStatus {
        UNREVIEWED(0,"未审核"),
        REVIEW_FAILED(20,"审核失败"),
        APPROVED(10,"审核通过");
        private final int id;
        private final String name;
    }
}