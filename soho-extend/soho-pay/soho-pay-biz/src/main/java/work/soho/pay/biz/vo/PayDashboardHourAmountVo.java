package work.soho.pay.biz.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 支付看板分时金额点位。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("支付看板分时金额点位")
public class PayDashboardHourAmountVo {
    /**
     * 小时标签，格式 HH:00。
     */
    @ApiModelProperty("小时标签，格式 HH:00")
    private String hour;

    /**
     * 金额。
     */
    @ApiModelProperty("金额")
    private BigDecimal amount;
}
