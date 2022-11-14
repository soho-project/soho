package work.soho.pay.biz.platform.alipay.utils;

import work.soho.pay.biz.platform.model.PayOrderDetails;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class AlipayHelpUtil {
    /**
     * 从请求hashmap数据获取 PayOrderDetails
     *
     * @param data
     * @return
     * @throws ParseException
     */
    public static PayOrderDetails fromHashMap(HashMap<String, String> data) throws ParseException {
        PayOrderDetails payOrderDetails = new PayOrderDetails();
        payOrderDetails.setAmount(new BigDecimal(data.get("total_amount")));
        payOrderDetails.setOutTradeNo(data.get("out_trade_no"));
        payOrderDetails.setTransactionId("trade_no");
        payOrderDetails.setPaySuccessTime((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(data.get("gmt_payment")));
        String payState = data.get("trade_status");
        if(payState.equals("TRADE_SUCCESS")) {
            payOrderDetails.setTradeState(PayOrderDetails.TradeStateEnum.SUCCESS.getState());
        } else {
            //支付关闭，支付失败
            payOrderDetails.setTradeState(PayOrderDetails.TradeStateEnum.CLOSED.getState());
        }

        return payOrderDetails;
    }
}
