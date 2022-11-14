package work.soho.pay.biz.platform.wechat.adapter;


import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.h5.model.*;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.IpUtils;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.platform.payapis.Pay;

import java.util.HashMap;
import java.util.Map;

public class H5Apis extends AbstractApis implements Pay {

    public H5Apis(PayConfig payConfig) {
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
        H5Info h5Info = new H5Info();
        h5Info.setType("Wap");
        sceneInfo.setH5Info(h5Info);

        request.setSceneInfo(sceneInfo);

        H5Service service = new H5Service.Builder().httpClient(getHttpClient()).build();
        PrepayResponse response = service.prepay(request);
        result.put("h5_url", response.getH5Url());
        return result;
    }
}
