package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ShopFreightTemplateEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Type {
        BY_QUANTITY(1,"按件数"),
        BY_WEIGHT(2,"按重量"),
        BY_VOLUME(3,"按体积"),
        FLAT_SHIPPING_RATE(4,"固定运费");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum ValuationMethod {
        BY_PRODUCT(1,"按商品"),
        BY_ORDER(2,"按订单");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum IsFreeShipping {
        NO(0,"否"),
        YES(1,"是");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum FreeConditionType {
        BY_NO(0,"无条件"),
        BY_AMOUNT(1,"按金额"),
        BY_QUANTITY(2,"按件数"),
        BY_WEIGHT(3,"按重量");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum IncludeSpecialRegions {
        NO(0,"否"),
        YES(1,"是");
        private final int id;
        private final String name;
    }
}