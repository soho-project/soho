package work.soho.pay.biz.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付方式分时金额统计结果。
 */
@Data
public class PayDashboardPayMethodHourAmountStatsDTO {
    /**
     * 支付方式ID。
     */
    private Integer payId;

    /**
     * 小时，取值 0-23。
     */
    private Integer hour;

    /**
     * 金额汇总。
     */
    private BigDecimal amount;
}
