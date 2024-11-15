package work.soho.quartz.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class AdminJobLogEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        IN_PROGRESS(1,"执行中"),
        EXECUTION_COMPLETED(2,"执行完成"),
        TERMINATION_OF_EXECUTION(3,"执行终止");
        private final int id;
        private final String name;
    }
}