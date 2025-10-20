package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ShopProductModelSpecEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Type {
        SINGLE_CHOICE(20,"单选"),
        TEXT_INPUT(10,"文本输入");
        private final int id;
        private final String name;
    }
}