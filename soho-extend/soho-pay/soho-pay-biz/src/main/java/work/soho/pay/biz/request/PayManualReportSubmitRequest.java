package work.soho.pay.biz.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户端提交自定义二维码支付上报请求。
 */
@Data
public class PayManualReportSubmitRequest {
    /**
     * 支付方式 ID。
     */
    @ApiModelProperty(value = "支付方式ID", required = true)
    private Integer payInfoId;

    /**
     * 付款人姓名。
     */
    @ApiModelProperty(value = "付款人姓名", required = true)
    private String payerName;

    /**
     * 支付金额。
     */
    @ApiModelProperty(value = "支付金额", required = true)
    private BigDecimal payAmount;

    /**
     * 支付时间。
     */
    @ApiModelProperty(value = "支付时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 支付单号（用户侧上报单号，通常为三方流水号）。
     */
    @ApiModelProperty(value = "支付单号", required = true)
    private String payOrderNo;

    /**
     * 备注信息。
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 签名时间戳（毫秒）。
     */
    @ApiModelProperty(value = "签名时间戳(毫秒)", required = true)
    private String signTimestamp;

    /**
     * 签名随机串。
     */
    @ApiModelProperty(value = "签名随机串", required = true)
    private String signNonce;

    /**
     * 签名值。
     */
    @ApiModelProperty(value = "签名值", required = true)
    private String sign;
}
