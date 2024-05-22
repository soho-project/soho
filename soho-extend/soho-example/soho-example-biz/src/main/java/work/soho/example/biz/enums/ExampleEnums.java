package work.soho.example.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ExampleEnums {

    @RequiredArgsConstructor
    @Getter
    public enum ApplyStatus {
        UNAPPROVED(0,"未审批"),
        APPROVED(1,"已审批");
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