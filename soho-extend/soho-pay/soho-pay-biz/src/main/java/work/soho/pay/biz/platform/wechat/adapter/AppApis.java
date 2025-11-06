package work.soho.pay.biz.platform.wechat.adapter;


import com.wechat.pay.java.service.payments.app.AppService;
import com.wechat.pay.java.service.payments.app.model.Amount;
import com.wechat.pay.java.service.payments.app.model.PrepayRequest;
import com.wechat.pay.java.service.payments.app.model.PrepayResponse;
import com.wechat.pay.java.service.payments.app.model.SceneInfo;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.IpUtils;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.platform.payapis.Pay;

import java.util.HashMap;
import java.util.Map;

public class AppApis extends AbstractApis implements Pay {

    public AppApis(PayConfig payConfig) {
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

        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setPayerClientIp(IpUtils.getClientIp());
        request.setSceneInfo(sceneInfo);

        AppService service = new AppService.Builder().httpClient(getHttpClient()).build();
        PrepayResponse response = service.prepay(request);
        result.put("prepay_id", response.getPrepayId());
        return result;
    }
}
