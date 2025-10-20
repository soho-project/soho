package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ShopRegionEnums {

    @RequiredArgsConstructor
    @Getter
    public enum RegionLevel {
        PROVINCE(1,"省"),
        CITY(2,"市"),
        DISTRICT(3,"区");
        private final int id;
        private final String name;
    }
}