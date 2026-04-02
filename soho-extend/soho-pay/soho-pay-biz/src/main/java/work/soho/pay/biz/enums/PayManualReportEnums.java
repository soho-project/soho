package work.soho.pay.biz.enums;

import lombok.Getter;

/**
 * 自定义二维码支付上报状态枚举。
 */
public class PayManualReportEnums {
    /**
     * 匹配状态枚举。
     */
    @Getter
    public enum MatchStatus {
        WAIT_MATCH(0, "待匹配"),
        AUTO_MATCHED(1, "自动匹配成功"),
        WAIT_REVIEW(2, "待人工审核"),
        MANUAL_APPROVED(3, "人工审核通过"),
        MANUAL_REJECTED(4, "人工审核拒绝");

        private final int code;
        private final String name;

        MatchStatus(int code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}
