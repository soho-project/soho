package work.soho.ai.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class AiApiCallLogEnums {
    @RequiredArgsConstructor
    @Getter
    public enum Status {
        FAILED(0, "失败"),
        SUCCESS(1, "成功");

        private final int id;
        private final String name;
    }
}
