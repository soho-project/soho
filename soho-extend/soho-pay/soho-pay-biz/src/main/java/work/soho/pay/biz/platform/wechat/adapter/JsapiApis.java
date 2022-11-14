package work.soho.pay.biz.platform.wechat.adapter;

import com.wechat.pay.java.core.cipher.RSASigner;
import com.wechat.pay.java.core.util.NonceUtil;
import com.wechat.pay.java.core.util.PemUtil;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.platform.payapis.Pay;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JsapiApis extends AbstractApis implements Pay {

    public JsapiApis(PayConfig config) {
        super(config);
    }

    /**
     * 支付接口
     *
     * @return
     */
    public Map<String, String> pay(Order order) {
        JsapiService service = new JsapiService.Builder().httpClient(getHttpClient()).build();
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
        Payer payer = new Payer();
        payer.setOpenid(order.getOpenId());
        request.setPayer(payer);

        try {
            PrepayResponse response = service.prepay(request);
            return sign(response.getPrepayId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取签名数据
     *
     * @param prepayId
     * @return
     */
    private HashMap<String, String> sign(String prepayId) {
        HashMap<String, String> result = new HashMap<>();
        String nonce = NonceUtil.createNonce(32);
        Integer ts = Integer.valueOf(String.valueOf((new Date()).getTime()/1000));
        RSASigner signer = new RSASigner(payConfig.getMerchantSerialNumber(), PemUtil.loadPrivateKeyFromString(payConfig.getPrivateKey()));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(payConfig.getAppId())
                .append('\n')
                .append(ts)
                .append('\n')
                .append(nonce);
        String sign = signer.sign(stringBuffer.toString()).getSign();
        result.put("appId", payConfig.getAppId());
        result.put("timestamp", String.valueOf(ts));
        result.put("nonceStr", nonce);
        result.put("package", "prepay_id=" + prepayId);
        result.put("paySign", sign);
        result.put("signType", "RSA");
        return result;
    }
}
