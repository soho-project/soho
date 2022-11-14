package work.soho.pay.biz.platform.wechat.utils;

import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.SneakyThrows;
import work.soho.pay.biz.platform.model.PayOrderDetails;

import java.text.SimpleDateFormat;

public class HelpUtil {
    /**
     * 微信业务单赚项目本地三方支付单信息
     *
     * @param transaction
     * @return
     */
    @SneakyThrows
    public static PayOrderDetails fromTransaction(Transaction transaction) {
        PayOrderDetails payOrderDetails = new PayOrderDetails();
        payOrderDetails.setOutTradeNo(transaction.getOutTradeNo());
        payOrderDetails.setPaySuccessTime((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")).parse(transaction.getSuccessTime()));
        payOrderDetails.setTransactionId(transaction.getTransactionId());
        payOrderDetails.setTradeState(getTradeState(transaction).getState());
        payOrderDetails.setTradeType(transaction.getTradeType().name());
        return payOrderDetails;
    }

    /**
     * 解析获取支付状态
     *
     * @param transaction
     * @return
     */
    public static PayOrderDetails.TradeStateEnum getTradeState(Transaction transaction) {
        if(Transaction.TradeStateEnum.SUCCESS.compareTo(transaction.getTradeState()) == 0 ) {
            return PayOrderDetails.TradeStateEnum.SUCCESS;
        } else if (Transaction.TradeStateEnum.CLOSED.compareTo(transaction.getTradeState()) == 0) {
            return PayOrderDetails.TradeStateEnum.CLOSED;
        } else if ( Transaction.TradeStateEnum.REFUND.compareTo(transaction.getTradeState()) == 0) {
            return PayOrderDetails.TradeStateEnum.REFUND;
        } else {
            return PayOrderDetails.TradeStateEnum.NOTPAY;
        }
    }
}
