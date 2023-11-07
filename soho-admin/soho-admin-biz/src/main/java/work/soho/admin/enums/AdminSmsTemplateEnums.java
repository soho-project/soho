package work.soho.admin.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class AdminSmsTemplateEnums {
    @RequiredArgsConstructor
    @Getter
    public enum AdapterName {
        ALIYUN("aliyun","aliyun"),
        TENCENT("tencent","tencent");
        private final String id;
        private final String name;
    }
}
