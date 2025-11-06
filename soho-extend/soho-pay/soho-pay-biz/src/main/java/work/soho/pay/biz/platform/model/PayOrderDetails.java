package work.soho.pay.biz.platform.model;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayOrderDetails {
    /**
     * 微信支付单业务单号
     */
    private String transactionId;

    /**
     * 外部跟踪单号；本地支付单号
     */
    private String outTradeNo;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付成功时间
     */
    private Date paySuccessTime;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 交易状态
     */
    private Integer tradeState;

    /**
     * 状态枚举
     */
    @Getter
    public enum TradeStateEnum {
        SUCCESS(1, "支付成功"),
        REFUND(2, "转入退款"),
        NOTPAY(3, "未支付"),
        CLOSED(4, "已关闭"),
        REVOKED(5, "已撤销"),
        USERPAYING(6, "用户支付中"),
        PAYERROR(7, "支付失败");

        private int state;
        private String name;

        TradeStateEnum(int state, String name) {
            this.state = state;
            this.name = name;
        }

        /**
         * 根据值获取枚举
         *
         * @param state
         * @return
         */
        public TradeStateEnum getByState(int state) {
            for(TradeStateEnum item: values()) {
                if(item.state == state) {
                    return item;
                }
            }
            return null;
        }
    }
}
