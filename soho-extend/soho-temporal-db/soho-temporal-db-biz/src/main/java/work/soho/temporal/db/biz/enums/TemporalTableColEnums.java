package work.soho.temporal.db.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class TemporalTableColEnums {

    @RequiredArgsConstructor
    @Getter
    public enum UpdatedTime {;
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum CreatedTime {;
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        DISABLED(0,"禁用"),
        ACTIVE(1,"活跃");
        private final int id;
        private final String name;
    }
}