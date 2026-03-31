package work.soho.pay.biz.platform.paypal.adapter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.platform.model.PayOrderDetails;
import work.soho.pay.biz.platform.payapis.Pay;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaypalWebApis implements Pay {
    private static final String DEFAULT_API_URL = "https://api-m.sandbox.paypal.com";
    private static final String DEFAULT_CURRENCY = "USD";

    private final PayConfig payConfig;

    public PaypalWebApis(PayConfig payConfig) {
        this.payConfig = payConfig;
    }

    @Override
    public Map<String, String> pay(Order order) throws Exception {
        String accessToken = getAccessToken();

        JSONObject body = new JSONObject();
        body.put("intent", "CAPTURE");

        JSONObject amount = new JSONObject();
        amount.put("currency_code", getCurrencyCode());
        amount.put("value", formatAmount(order.getAmount()));

        JSONObject purchaseUnit = new JSONObject();
        purchaseUnit.put("invoice_id", order.getOutTradeNo());
        purchaseUnit.put("custom_id", order.getOutTradeNo());
        purchaseUnit.put("description", StrUtil.blankToDefault(order.getDescription(), "支付订单"));
        purchaseUnit.put("amount", amount);

        JSONArray purchaseUnits = new JSONArray();
        purchaseUnits.add(purchaseUnit);
        body.put("purchase_units", purchaseUnits);

        String callbackUrl = StrUtil.blankToDefault(order.getNotifyUrl(), "https://example.com");
        JSONObject context = new JSONObject();
        context.put("shipping_preference", "NO_SHIPPING");
        context.put("user_action", "PAY_NOW");
        context.put("return_url", callbackUrl + "?state=success");
        context.put("cancel_url", callbackUrl + "?state=cancel");
        body.put("application_context", context);

        HttpResponse response = HttpRequest.post(getApiBaseUrl() + "/v2/checkout/orders")
                .header(Header.AUTHORIZATION, "Bearer " + accessToken)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .body(body.toString())
                .execute();
        if(response.getStatus() >= 300) {
            throw new RuntimeException("paypal 下单失败: " + response.body());
        }

        JSONObject json = JSONUtil.parseObj(response.body());
        String paypalOrderId = json.getStr("id");
        String approveUrl = getApproveUrl(json.getJSONArray("links"));

        HashMap<String, String> result = new HashMap<>();
        result.put("pay_type", "paypal");
        result.put("paypal_order_id", paypalOrderId);
        result.put("approve_url", approveUrl);
        result.put("h5_url", approveUrl);
        return result;
    }

    public boolean verifyWebhook(Map<String, String> headers, String webhookBody) {
        String webhookId = getWebhookId();
        if(StrUtil.isBlank(webhookId)) {
            return false;
        }
        String transmissionId = getHeaderIgnoreCase(headers, "paypal-transmission-id");
        String transmissionTime = getHeaderIgnoreCase(headers, "paypal-transmission-time");
        String certUrl = getHeaderIgnoreCase(headers, "paypal-cert-url");
        String authAlgo = getHeaderIgnoreCase(headers, "paypal-auth-algo");
        String transmissionSig = getHeaderIgnoreCase(headers, "paypal-transmission-sig");
        if(StrUtil.hasBlank(transmissionId, transmissionTime, certUrl, authAlgo, transmissionSig)) {
            return false;
        }

        JSONObject body = new JSONObject();
        body.put("transmission_id", transmissionId);
        body.put("transmission_time", transmissionTime);
        body.put("cert_url", certUrl);
        body.put("auth_algo", authAlgo);
        body.put("transmission_sig", transmissionSig);
        body.put("webhook_id", webhookId);
        body.put("webhook_event", JSONUtil.parseObj(webhookBody));

        String accessToken = getAccessToken();
        HttpResponse response = HttpRequest.post(getApiBaseUrl() + "/v1/notifications/verify-webhook-signature")
                .header(Header.AUTHORIZATION, "Bearer " + accessToken)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .body(body.toString())
                .execute();
        if(response.getStatus() >= 300) {
            return false;
        }
        JSONObject json = JSONUtil.parseObj(response.body());
        return "SUCCESS".equalsIgnoreCase(json.getStr("verification_status"));
    }

    public PayOrderDetails captureFromWebhook(String webhookBody) {
        JSONObject body = JSONUtil.parseObj(webhookBody);
        String eventType = body.getStr("event_type");
        if(StrUtil.isBlank(eventType)) {
            return null;
        }
        if("CHECKOUT.ORDER.APPROVED".equals(eventType)) {
            JSONObject resource = body.getJSONObject("resource");
            if(resource == null) {
                return null;
            }
            String paypalOrderId = resource.getStr("id");
            if(StrUtil.isBlank(paypalOrderId)) {
                return null;
            }
            return captureOrder(paypalOrderId);
        }
        if("PAYMENT.CAPTURE.COMPLETED".equals(eventType)) {
            JSONObject resource = body.getJSONObject("resource");
            if(resource == null) {
                return null;
            }
            PayOrderDetails details = new PayOrderDetails();
            details.setTradeState(PayOrderDetails.TradeStateEnum.SUCCESS.getState());
            details.setTransactionId(resource.getStr("id"));
            details.setOutTradeNo(StrUtil.blankToDefault(resource.getStr("custom_id"), resource.getStr("invoice_id")));
            JSONObject amount = resource.getJSONObject("amount");
            if(amount != null) {
                details.setAmount(new BigDecimal(amount.getStr("value", "0")));
            }
            details.setPaySuccessTime(new Date());
            details.setTradeType("paypal_web");
            if(StrUtil.isBlank(details.getOutTradeNo())) {
                String orderId = getRelatedOrderId(resource);
                if(StrUtil.isNotBlank(orderId)) {
                    PayOrderDetails fromOrder = queryOrderDetails(orderId);
                    if(fromOrder != null) {
                        return fromOrder;
                    }
                }
            }
            return details;
        }
        return null;
    }

    private PayOrderDetails captureOrder(String paypalOrderId) {
        String accessToken = getAccessToken();
        HttpResponse response = HttpRequest.post(getApiBaseUrl() + "/v2/checkout/orders/" + paypalOrderId + "/capture")
                .header(Header.AUTHORIZATION, "Bearer " + accessToken)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .body("{}")
                .execute();
        if(response.getStatus() >= 300) {
            throw new RuntimeException("paypal capture 失败: " + response.body());
        }
        JSONObject json = JSONUtil.parseObj(response.body());
        return parseCaptureResult(json);
    }

    private PayOrderDetails queryOrderDetails(String paypalOrderId) {
        String accessToken = getAccessToken();
        HttpResponse response = HttpRequest.get(getApiBaseUrl() + "/v2/checkout/orders/" + paypalOrderId)
                .header(Header.AUTHORIZATION, "Bearer " + accessToken)
                .execute();
        if(response.getStatus() >= 300) {
            return null;
        }
        JSONObject json = JSONUtil.parseObj(response.body());
        JSONArray units = json.getJSONArray("purchase_units");
        if(units == null || units.isEmpty()) {
            return null;
        }
        JSONObject unit = units.getJSONObject(0);
        if(unit == null) {
            return null;
        }
        PayOrderDetails details = new PayOrderDetails();
        details.setTradeState(PayOrderDetails.TradeStateEnum.SUCCESS.getState());
        details.setOutTradeNo(StrUtil.blankToDefault(unit.getStr("custom_id"), unit.getStr("invoice_id")));
        details.setTradeType("paypal_web");
        details.setPaySuccessTime(new Date());
        JSONObject amount = unit.getJSONObject("amount");
        if(amount != null) {
            details.setAmount(new BigDecimal(amount.getStr("value", "0")));
        }
        return details;
    }

    private PayOrderDetails parseCaptureResult(JSONObject captureResponse) {
        PayOrderDetails details = new PayOrderDetails();
        details.setTradeState(PayOrderDetails.TradeStateEnum.SUCCESS.getState());
        details.setTradeType("paypal_web");
        details.setPaySuccessTime(new Date());

        JSONArray units = captureResponse.getJSONArray("purchase_units");
        if(units != null && !units.isEmpty()) {
            JSONObject unit = units.getJSONObject(0);
            if(unit != null) {
                details.setOutTradeNo(StrUtil.blankToDefault(unit.getStr("custom_id"), unit.getStr("invoice_id")));
                JSONObject amount = unit.getJSONObject("amount");
                if(amount != null) {
                    details.setAmount(new BigDecimal(amount.getStr("value", "0")));
                }
                JSONObject payments = unit.getJSONObject("payments");
                if(payments != null) {
                    JSONArray captures = payments.getJSONArray("captures");
                    if(captures != null && !captures.isEmpty()) {
                        JSONObject capture = captures.getJSONObject(0);
                        if(capture != null) {
                            details.setTransactionId(capture.getStr("id"));
                            JSONObject capturedAmount = capture.getJSONObject("amount");
                            if(capturedAmount != null) {
                                details.setAmount(new BigDecimal(capturedAmount.getStr("value", "0")));
                            }
                        }
                    }
                }
            }
        }
        return details;
    }

    private String getAccessToken() {
        String clientId = payConfig.getAppId();
        String secret = payConfig.getPrivateKey();
        String auth = Base64.getEncoder().encodeToString((clientId + ":" + secret).getBytes(StandardCharsets.UTF_8));
        HttpResponse response = HttpRequest.post(getApiBaseUrl() + "/v1/oauth2/token")
                .header(Header.AUTHORIZATION, "Basic " + auth)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .body("grant_type=client_credentials")
                .execute();
        if(response.getStatus() >= 300) {
            throw new RuntimeException("paypal 获取 token 失败: " + response.body());
        }
        JSONObject json = JSONUtil.parseObj(response.body());
        String token = json.getStr("access_token");
        if(StrUtil.isBlank(token)) {
            throw new RuntimeException("paypal 获取 token 返回为空");
        }
        return token;
    }

    private String getApiBaseUrl() {
        return StrUtil.blankToDefault(payConfig.getMerchantId(), DEFAULT_API_URL);
    }

    private String getCurrencyCode() {
        return StrUtil.blankToDefault(payConfig.getMerchantSerialNumber(), DEFAULT_CURRENCY).toUpperCase();
    }

    private String getWebhookId() {
        return payConfig.getPayCertificate();
    }

    private String formatAmount(BigDecimal amount) {
        if(amount == null) {
            return "0.00";
        }
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String getApproveUrl(JSONArray links) {
        if(links == null) {
            return null;
        }
        for (Object obj : links) {
            JSONObject link = (JSONObject) obj;
            if(link != null && "approve".equals(link.getStr("rel"))) {
                return link.getStr("href");
            }
        }
        return null;
    }

    private String getHeaderIgnoreCase(Map<String, String> headers, String headerName) {
        if(headers == null || headers.isEmpty()) {
            return null;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if(entry.getKey() != null && entry.getKey().equalsIgnoreCase(headerName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String getRelatedOrderId(JSONObject resource) {
        JSONObject supplementaryData = resource.getJSONObject("supplementary_data");
        if(supplementaryData == null) {
            return null;
        }
        JSONObject relatedIds = supplementaryData.getJSONObject("related_ids");
        if(relatedIds == null) {
            return null;
        }
        return relatedIds.getStr("order_id");
    }
}
