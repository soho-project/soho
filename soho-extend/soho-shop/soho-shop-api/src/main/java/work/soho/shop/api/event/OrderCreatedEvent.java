package work.soho.shop.api.event;

import lombok.Data;

/**
 * 订单创建事件
 */
@Data
public class OrderCreatedEvent {
    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;
}
