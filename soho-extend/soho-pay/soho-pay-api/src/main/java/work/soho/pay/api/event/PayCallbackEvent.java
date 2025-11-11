package work.soho.pay.api.event;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
public class PayCallbackEvent {
    /**
     * 支付模块支付单号
     */
    private String orderNo;

    /**
     * 创建支付单的时候传递的业务方单号
     */
    private String outTradeNo;

    /**
     * 支付单状态
     */
    private Integer status;

    // 支付信息ID
    private Integer payInfoId;

    // 交易流水号
    private String transactionNo;

    @Getter
    public static enum PayOrderStatus {
        SUCCESS(1, "支付成功"),
        REFUND(2, "转入退款"),
        NOTPAY(3, "未支付"),
        CLOSED(4, "已关闭"),
        REVOKED(5, "已撤销"),
        USERPAYING(6, "用户支付中"),
        PAYERROR(7, "支付失败");

        private final int id;
        private final String name;

        // 添加枚举构造方法
        PayOrderStatus(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
