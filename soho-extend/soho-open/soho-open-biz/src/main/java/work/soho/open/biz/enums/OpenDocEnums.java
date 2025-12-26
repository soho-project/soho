package work.soho.open.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OpenDocEnums {

    @RequiredArgsConstructor
    @Getter
    public enum ContentFormat {
        HTML(1,"html"),
        MARKDOWN(2,"markdown");
        private final int id;
        private final String name;
    }

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
    public enum CategoryType {
        CONTENT(1,"内容"),
        API(2,"API"),
        CATALOGUE(3,"目录");
        private final int id;
        private final String name;
    }
}