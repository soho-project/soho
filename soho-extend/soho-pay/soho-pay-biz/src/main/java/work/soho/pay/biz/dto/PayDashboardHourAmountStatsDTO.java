package work.soho.pay.biz.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付看板分时金额统计结果。
 */
@Data
public class PayDashboardHourAmountStatsDTO {
    /**
     * 小时，取值 0-23。
     */
    private Integer hour;

    /**
     * 金额汇总。
     */
    private BigDecimal amount;
}
