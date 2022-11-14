package work.soho.pay.biz.platform.wechat.adapter;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAConfig;
import com.wechat.pay.java.core.util.PemUtil;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.wechat.http.SohoHttpClient;
import work.soho.pay.biz.platform.model.Order;

import java.math.BigDecimal;
import java.util.Map;

abstract public class AbstractApis {
    protected PayConfig payConfig;

    public AbstractApis(PayConfig payConfig) {
        this.payConfig = payConfig;
    }

    abstract Map<String, String> pay(Order order);

    protected Integer getIntAmount(Order order) {
        return order.getAmount().multiply(new BigDecimal("100")).intValue();
    }

    /**
     * 获取http客户端
     *
     * @return
     */
    protected SohoHttpClient getHttpClient() {
        return new SohoHttpClient(getSdkConfig().createCredential(), getSdkConfig().createValidator());
    }

    /**
     * 获取sdk config
     *
     * @return
     */
    private Config getSdkConfig() {
        Config sdkConfig =
                new RSAConfig.Builder()
                        .merchantId(payConfig.getMerchantId())
                        .privateKey(PemUtil.loadPrivateKeyFromString(payConfig.getPrivateKey()))
                        .merchantSerialNumber(payConfig.getMerchantSerialNumber())
                        //TODO 多个平台证书支持
                        .wechatPayCertificates(PemUtil.loadX509FromString(payConfig.getPayCertificate()))
                        .build();

        return sdkConfig;
    }
}
