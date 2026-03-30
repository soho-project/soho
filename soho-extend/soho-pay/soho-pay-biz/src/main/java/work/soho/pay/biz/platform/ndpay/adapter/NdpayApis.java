package work.soho.pay.biz.platform.ndpay.adapter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import work.soho.common.core.util.IpUtils;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.platform.model.PayOrderDetails;
import work.soho.pay.biz.platform.payapis.Pay;
import work.soho.pay.biz.platform.payapis.QueryOrder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class NdpayApis implements Pay, QueryOrder {
    private static final String DEFAULT_PAY_URL = "https://pay.jeepay.vip";
    private static final String DEFAULT_WAY_CODE = "WEB_CASHIER";

    private final PayConfig payConfig;

    public NdpayApis(PayConfig payConfig) {
        this.payConfig = payConfig;
    }

    @Override
    public Map<String, String> pay(Order order) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("mchNo", payConfig.getMerchantId());
        params.put("appId", payConfig.getAppId());
        params.put("mchOrderNo", order.getOutTradeNo());
        params.put("wayCode", getWayCode());
        params.put("amount", getCentAmount(order.getAmount()));
        params.put("currency", "cny");
        params.put("clientIp", IpUtils.getClientIp());
        params.put("subject", getSubject(order));
        params.put("body", getBody(order));
        params.put("notifyUrl", order.getNotifyUrl());
        params.put("reqTime", System.currentTimeMillis());
        params.put("version", "1.0");
        params.put("signType", "MD5");
        params.put("sign", NdpaySignUtil.sign(params, payConfig.getPrivateKey()));

        String responseText = HttpUtil.post(getUnifiedOrderUrl(), params);
        JSONObject json = JSONUtil.parseObj(responseText);
        if(json.getInt("code", -1) != 0) {
            throw new RuntimeException("ndpay 下单失败: " + json.getStr("msg", "unknown"));
        }

        JSONObject data = json.getJSONObject("data");
        HashMap<String, String> result = new HashMap<>();
        if(data == null) {
            return result;
        }

        String payDataType = data.getStr("payDataType");
        String payData = data.getStr("payData");
        result.put("payOrderId", data.getStr("payOrderId"));
        result.put("payDataType", payDataType);
        result.put("payData", payData);
        if(StrUtil.equals(payDataType, "form")) {
            result.put("body", payData);
        } else if(StrUtil.equals(payDataType, "payUrl")) {
            result.put("h5_url", payData);
        }
        return result;
    }

    @Override
    public PayOrderDetails queryOrder(String outTradeNo) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("mchNo", payConfig.getMerchantId());
        params.put("appId", payConfig.getAppId());
        params.put("mchOrderNo", outTradeNo);
        params.put("reqTime", System.currentTimeMillis());
        params.put("version", "1.0");
        params.put("signType", "MD5");
        params.put("sign", NdpaySignUtil.sign(params, payConfig.getPrivateKey()));

        String responseText = HttpUtil.post(getPayUrl() + "/api/pay/query", params);
        JSONObject json = JSONUtil.parseObj(responseText);
        PayOrderDetails details = new PayOrderDetails();
        details.setOutTradeNo(outTradeNo);
        details.setTradeState(PayOrderDetails.TradeStateEnum.NOTPAY.getState());
        if(json.getInt("code", -1) != 0) {
            return details;
        }
        JSONObject data = json.getJSONObject("data");
        if(data == null) {
            return details;
        }
        details.setOutTradeNo(data.getStr("mchOrderNo", outTradeNo));
        details.setTransactionId(data.getStr("channelOrderNo", data.getStr("payOrderId")));
        Integer amountCent = data.getInt("amount");
        if(amountCent != null) {
            details.setAmount(new BigDecimal(amountCent).divide(new BigDecimal("100")));
        }
        details.setTradeType(data.getStr("wayCode"));
        details.setTradeState(mapOrderState(data.getInt("orderState", data.getInt("state", 0))));
        details.setPaySuccessTime(new Date());
        return details;
    }

    private String getUnifiedOrderUrl() {
        return getPayUrl() + "/api/pay/unifiedOrder";
    }

    private String getWayCode() {
        return StrUtil.blankToDefault(payConfig.getMerchantSerialNumber(), DEFAULT_WAY_CODE);
    }

    private String getPayUrl() {
        return StrUtil.blankToDefault(payConfig.getPayCertificate(), DEFAULT_PAY_URL);
    }

    private String getSubject(Order order) {
        return StrUtil.maxLength(getBody(order), 64);
    }

    private String getBody(Order order) {
        return StrUtil.maxLength(StrUtil.blankToDefault(order.getDescription(), "支付订单"), 256);
    }

    private int getCentAmount(BigDecimal amount) {
        return amount.multiply(new BigDecimal("100")).intValue();
    }

    private Integer mapOrderState(Integer orderState) {
        if(orderState == null) {
            return PayOrderDetails.TradeStateEnum.NOTPAY.getState();
        }
        switch (orderState) {
            case 2:
                return PayOrderDetails.TradeStateEnum.SUCCESS.getState();
            case 3:
                return PayOrderDetails.TradeStateEnum.PAYERROR.getState();
            case 6:
                return PayOrderDetails.TradeStateEnum.CLOSED.getState();
            default:
                return PayOrderDetails.TradeStateEnum.NOTPAY.getState();
        }
    }
}
