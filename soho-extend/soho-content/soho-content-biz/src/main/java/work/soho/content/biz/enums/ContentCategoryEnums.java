package work.soho.content.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ContentCategoryEnums {

    @RequiredArgsConstructor
    @Getter
    public enum IsDisplay {
        DO_NOT_DISPLAY(0,"不显示"),
        DISPLAY(1,"显示");
        private final int id;
        private final String name;
    }
}