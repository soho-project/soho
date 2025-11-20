package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ShopUserReviewsEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        PENDING_REVIEW(1,"待审核"),
        APPROVED(2,"审核通过"),
        APPROVAL_REJECTED(3,"审核不通过"),
        USER_ACTIVELY_HIDES(4,"用户主动隐藏");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum IsTop {
        NO(0,"否"),
        YES(1,"是");
        private final int id;
        private final String name;
    }
}