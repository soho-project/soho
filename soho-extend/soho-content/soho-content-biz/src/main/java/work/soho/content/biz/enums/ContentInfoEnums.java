package work.soho.content.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ContentInfoEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        HIDE(0,"隐藏"),
        DISPLAY(1,"显示");
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