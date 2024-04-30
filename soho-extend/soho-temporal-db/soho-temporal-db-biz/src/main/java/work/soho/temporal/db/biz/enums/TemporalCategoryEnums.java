package work.soho.temporal.db.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class TemporalCategoryEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Id {;
        private final int id;
        private final String name;
    }
}