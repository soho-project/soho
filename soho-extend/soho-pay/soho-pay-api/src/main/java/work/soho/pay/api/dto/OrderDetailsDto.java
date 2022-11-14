package work.soho.pay.api.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付请求数据格式
 */
@Data
public class OrderDetailsDto {
    /**
     * 支付方方式ID
     *
     * 必填
     */
    private Integer payInfoId;

    /**
     * 订单金额
     *
     * 必填
     */
    private BigDecimal amount;

    /**
     * 商品描述 说明：商品描述
     *
     * 必填
     */
    private String description;

    /**
     * 商户订单号 说明：商户订单号
     *
     * 必填；调用方订单号
     * 唯一单号
     */
    private String outTradeNo;

    /**
     * 通知地址
     *
     * 选填；如果没有指定则使用支付配置表的默认地址
     */
    private String notifyUrl;

    /**
     * 支付用户openId
     *
     * 必填
     * 支付方案ID
     */
    private String openId;
}
