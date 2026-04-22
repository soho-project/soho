package work.soho.pay.biz.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付模块 Dashboard 首页数据。
 */
@Data
@ApiModel("支付模块 Dashboard 首页数据")
public class PayDashboardIndexVo {
    /**
     * 概览数据。
     */
    @ApiModelProperty("概览数据")
    private PayDashboardOverviewVo overview;

    /**
     * 今日支付金额分时曲线。
     */
    @ApiModelProperty("今日支付金额分时曲线")
    private List<PayDashboardHourAmountVo> todayAmountTrend = new ArrayList<>();

    /**
     * 各支付方式今日支付金额分时曲线。
     */
    @ApiModelProperty("各支付方式今日支付金额分时曲线")
    private List<PayDashboardPayMethodTrendVo> payMethodAmountTrends = new ArrayList<>();
}
