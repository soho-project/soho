package work.soho.open.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OpenAppEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        DISABLE(0, "禁用")
        ,AUDIT(1, "审核中")
        ,AUDIT_FAILED(2, "审核失败")
        ,ENABLE(3, "正常")
        ;

        private final int id;
        private final String name;
    }
}
