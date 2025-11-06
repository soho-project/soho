package work.soho.pay.api.event;

import lombok.Data;

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
}
