package work.soho.shop.api.request;

import lombok.Data;

@Data
public class CreatePayRequest {
    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 支付方式ID
     */
    private Integer payInfoId;

    /**
     * 支付用户openId
     */
    private String openId;
}
