package work.soho.pay.biz.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自定义二维码支付单轮询请求。
 */
@Data
public class PayManualOrderPollRequest {
    /**
     * 支付方式 ID。
     */
    @ApiModelProperty(value = "支付方式ID", required = true)
    private Integer payInfoId;

    /**
     * 上次已消费到的支付单 ID，首次轮询可不传或传 0。
     */
    @ApiModelProperty(value = "上次已消费到的支付单ID")
    private Integer lastOrderId;

    /**
     * 本次最多拉取条数，默认 20，最大 100。
     */
    @ApiModelProperty(value = "本次最多拉取条数")
    private Integer limit;

    /**
     * 长轮询等待秒数，默认 25 秒，最大 55 秒。
     */
    @ApiModelProperty(value = "长轮询等待秒数")
    private Integer waitSeconds;

    /**
     * 签名时间戳（毫秒）。
     */
    @ApiModelProperty(value = "签名时间戳", required = true)
    private String signTimestamp;

    /**
     * 签名随机串。
     */
    @ApiModelProperty(value = "签名随机串", required = true)
    private String signNonce;

    /**
     * 请求签名。
     */
    @ApiModelProperty(value = "签名", required = true)
    private String sign;
}
