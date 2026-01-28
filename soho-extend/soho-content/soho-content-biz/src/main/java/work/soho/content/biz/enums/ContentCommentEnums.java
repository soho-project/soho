package work.soho.content.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ContentCommentEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        DISABLED("0","禁用"),
        ACTIVE("1","活跃");
        private final String id;
        private final String name;
    }
}