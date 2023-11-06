package work.soho.open.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OpenCodeEnums {

    @RequiredArgsConstructor
    @Getter
    public enum IsLogin {
        NO(0, "NO"),
        YES(1, "YES");

        private final int id;
        private final String name;
    }
}
