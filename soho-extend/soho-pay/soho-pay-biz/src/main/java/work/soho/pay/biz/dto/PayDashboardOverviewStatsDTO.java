package work.soho.pay.biz.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付看板概览统计结果。
 */
@Data
public class PayDashboardOverviewStatsDTO {
    /**
     * 当日支付金额。
     */
    private BigDecimal todayPaidAmount;

    /**
     * 当日支付单数量。
     */
    private Long todayPaidOrderCount;

    /**
     * 当日未支付单数量。
     */
    private Long todayUnpaidOrderCount;
}
