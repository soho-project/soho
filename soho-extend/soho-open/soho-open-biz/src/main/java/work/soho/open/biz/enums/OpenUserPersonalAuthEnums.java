package work.soho.open.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OpenUserPersonalAuthEnums {

    @RequiredArgsConstructor
    @Getter
    public enum IdCardType {
        ID_CARD(1,"身份证"),
        PASSPORT(2,"护照"),
        HONG_KONG_AND_MACAU_EXITENTRY_PERMIT(3,"港澳通行证");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Gender {
        MALE(1,"男"),
        FEMALE(2,"女");
        private final int id;
        private final String name;
    }
}