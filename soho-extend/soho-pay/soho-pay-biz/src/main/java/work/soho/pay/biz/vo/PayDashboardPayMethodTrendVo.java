package work.soho.pay.biz.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付方式分时金额曲线。
 */
@Data
@ApiModel("支付方式分时金额曲线")
public class PayDashboardPayMethodTrendVo {
    /**
     * 支付方式ID。
     */
    @ApiModelProperty("支付方式ID")
    private Integer payId;

    /**
     * 支付方式标题。
     */
    @ApiModelProperty("支付方式标题")
    private String payTitle;

    /**
     * 分时金额点位。
     */
    @ApiModelProperty("分时金额点位")
    private List<PayDashboardHourAmountVo> points = new ArrayList<>();
}
