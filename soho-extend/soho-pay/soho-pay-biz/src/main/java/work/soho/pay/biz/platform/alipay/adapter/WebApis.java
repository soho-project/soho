package work.soho.pay.biz.platform.alipay.adapter;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.platform.payapis.Pay;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里wap支付
 */
public class WebApis extends AbstractAliapis implements Pay {
    public WebApis(PayConfig payConfig) {
        super(payConfig);
    }

    /**
     *
     * body:
     *
     *  <form name="punchout_form" method="post" action="https://openapi.alipay.com/gateway.do?charset=UTF8&method=alipay.trade.wap.pay&sign=u6mryNZrUxvnGQehTc7VrAHFrOGGrlY%2FvqC0Ni6Gk0oX4oytbLBd19Fa3XSVd6itQQCmYJn1b54eRftogzopVsBGCpK6Nxe14r%2FPzhkM9%2FKBptx3bGUd8HSDcZycfjSxG6qPp5nfH%2BeYIak%2F4pEZMYPUj22%2BKWccueM%2Bzpx2yxxBRYH1Z%2FGu2w7uT%2Bh1yaZmqB6W7WgUo3zuCBEt4boVIDUatkQxW6nuxDMPYsc6Koew3OgqcAGZ6XWT70HlOWQsl3FkN1kHN3BnP1M1SCNELBBo8F%2F4Z7ZlOoCzIEUjMUcbeLD0FHtJ14Ncc2cZ0%2F8FPLccAeVdWIp2j3K6qHwVgQ%3D%3D&version=1.0&app_id=2021003157632368&sign_type=RSA2&timestamp=2022-11-10+20%3A28%3A09&alipay_sdk=alipay-sdk-java-dynamicVersionNo&format=json">
     * <input type="hidden" name="biz_content" value="{&quot;out_trade_no&quot;:&quot;70501111111S001111119&quot;,&quot;product_code&quot;:&quot;QUICK_WAP_WAY&quot;,&quot;subject&quot;:&quot;大乐透&quot;,&quot;total_amount&quot;:&quot;0.01&quot;}">
     * <input type="submit" value="立即支付" style="display:none" >
     * </form>
     * <script>document.forms[0].submit();</script>
     *
     * @param order
     * @return
     * @throws AlipayApiException
     */
    @Override
    public Map<String, String> pay(Order order) throws AlipayApiException {
        AlipayClient alipayClient = getAliClient();
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl("");
        request.setReturnUrl("");
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", order.getOutTradeNo());
        bizContent.put("total_amount", order.getAmount().toString());
        bizContent.put("subject", order.getDescription());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        System.out.println(response.getBody());
        HashMap<String, String> result = new HashMap<>();
        if (response.isSuccess()) {
            result.put("body", response.getBody());
            return result;
        } else {
            return result;
        }
    }
}
