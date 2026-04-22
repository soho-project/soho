package work.soho.pay.biz.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付看板概览数据。
 */
@Data
@ApiModel("支付看板概览数据")
public class PayDashboardOverviewVo {
    /**
     * 当日支付金额。
     */
    @ApiModelProperty("当日支付金额")
    private BigDecimal todayPaidAmount;

    /**
     * 当日支付单数量。
     */
    @ApiModelProperty("当日支付单数量")
    private Long todayPaidOrderCount;

    /**
     * 当日未支付单数量。
     */
    @ApiModelProperty("当日未支付单数量")
    private Long todayUnpaidOrderCount;

    /**
     * 支付方式总数。
     */
    @ApiModelProperty("支付方式总数")
    private Long payMethodTotalCount;

    /**
     * 开启支付方式总数。
     */
    @ApiModelProperty("开启支付方式总数")
    private Long enabledPayMethodCount;

    /**
     * 禁用支付方式总数。
     */
    @ApiModelProperty("禁用支付方式总数")
    private Long disabledPayMethodCount;
}
