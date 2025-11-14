package work.soho.pay.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PayOrderVo {
    private Integer id;

    /**
     * 支付方式ID
     */
    @ApiModelProperty(value = "支付方式ID")
    private Integer payId;

    /**
     * 支付单号
     */
    @ApiModelProperty(value = "支付单号")
    private String orderNo;

    /**
     * 外部跟踪单号
     */
    @ApiModelProperty(value = "外部跟踪单号")
    private String trackingNo;

    /**
     * 支付供应商跟踪ID；例如微信，支付宝支付单号
     */
    @ApiModelProperty(value = "支付供应商跟踪ID；例如微信，支付宝支付单号")
    private String transactionId;

    /**
     * 支付金额
     */
    @ApiModelProperty(value = "支付金额")
    private BigDecimal amount;

    /**
     * 支付单状态;1:待支付,10:已扫码,20:支付成功,30:支付失败;frontType:select
     */
    @ApiModelProperty(value = "支付单状态;1:待支付,10:已扫码,20:支付成功,30:支付失败;")
    private Integer status;

    /**
     * 支付时间;;frontType:datetime
     */
    @ApiModelProperty(value = "支付时间")
    private LocalDateTime payedTime;

    /**
     * 订单客户端回调地址
     */
    @ApiModelProperty(value = "订单客户端回调地址")
    private String notifyUrl;

    /**
     * 用户ID
     *
     * 非必填，默认为 null
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 对应支付钱包用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userCustId;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedTime;
}
