package work.soho.open.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class OpenAuthApplyRecordsEnums {

    @RequiredArgsConstructor
    @Getter
    public enum AuthType {
        INDIVIDUAL(1,"个人"),
        ENTERPRISE(2,"企业");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum ApplyStatus {
        PENDING(0,"待处理"),
        UNDER_REVIEW(1,"审核中"),
        APPROVED(2,"审核通过"),
        REJECTED_AFTER_REVIEW(3,"审核拒绝"),
        CANCELED(4,"已取消"),
        TO_BE_SUBMITTED(5,"待提交");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum CallbackStatus {
        NO_CALLBACK(0,"未回调"),
        CALLBACK_SUCCEEDED(1,"回调成功"),
        CALLBACK_FAILED(2,"回调失败");
        private final int id;
        private final String name;
    }
}