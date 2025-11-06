package work.soho.pay.biz.platform.wechat.adapter;


import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest;
import lombok.SneakyThrows;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.platform.model.PayOrderDetails;
import work.soho.pay.biz.platform.payapis.Pay;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class NativeApis extends AbstractApis implements Pay {

    public NativeApis(PayConfig payConfig) {
        super(payConfig);
    }

    /**
     * 支付订单
     *
     * @return
     */
    public Map<String, String> pay(Order order) {
        HashMap<String, String> result = new HashMap<>();
        String outTradeNo = IDGeneratorUtils.snowflake().toString();
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(getIntAmount(order));
        request.setAmount(amount);
        request.setAppid(payConfig.getAppId());
        request.setMchid(payConfig.getMerchantId());
        request.setDescription(order.getDescription());
        request.setNotifyUrl(order.getNotifyUrl());
        request.setOutTradeNo(outTradeNo);

        NativePayService service = new NativePayService.Builder().httpClient(getHttpClient()).build();
        PrepayResponse response = service.prepay(request);
        result.put("code_url", response.getCodeUrl());
        return result;
    }

    /**
     * 获取支付单详情
     *
     * @param orderNo
     * @return
     */
    @SneakyThrows
    public PayOrderDetails orderDetailsByOutTradeNo(String orderNo) {
        PayOrderDetails payOrderDetails = new PayOrderDetails();
        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
        request.setOutTradeNo(orderNo);
        request.setMchid(payConfig.getMerchantId());
        NativePayService service = new NativePayService.Builder().httpClient(getHttpClient()).build();
        Transaction transaction = service.queryOrderByOutTradeNo(request);
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
    private PayOrderDetails.TradeStateEnum getTradeState(Transaction transaction) {
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
