package work.soho.pay.biz.platform.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {
    /**
     * 商品描述 说明：商品描述
     */
    private String description;

    /**
     * 商户订单号 说明：商户订单号
     */
    private String outTradeNo;

    /**
     * 附加数据 说明：附加数据
     */
    private String attach;

    /**
     * 通知地址 说明：有效性：1. HTTPS；2. 不允许携带查询串。
     */
    private String notifyUrl;

    /**
     * 订单优惠标记 说明：商品标记，代金券或立减优惠功能的参数。
     */
    private String goodsTag;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付用户openId
     */
    private String openId;

    // 下面是汇付通的一些参数

    /**
     * 用户钱包用户id
     */
    private String userCustId;
}
