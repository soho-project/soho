package work.soho.admin.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class AdminReleaseEnums {

    @RequiredArgsConstructor
    @Getter
    public enum PlatformType {
        WINDOWS(1,"Windows"),
        LINUX(2,"Linux");
        private final int id;
        private final String name;
    }
}