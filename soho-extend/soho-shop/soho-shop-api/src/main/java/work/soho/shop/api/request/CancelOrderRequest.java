package work.soho.shop.api.request;

import lombok.Data;

@Data
public class CancelOrderRequest {
    // 订单ID
    private Long orderId;

    // 取消原因
    private String reason;
}
