package work.soho.pay.biz.platform;

import work.soho.pay.biz.platform.alipay.adapter.WapApis;
import work.soho.pay.biz.platform.alipay.adapter.WebApis;
import work.soho.pay.biz.platform.payapis.HftCreateWallet;
import work.soho.pay.biz.platform.payapis.Pay;
import work.soho.pay.biz.platform.wechat.adapter.AppApis;
import work.soho.pay.biz.platform.wechat.adapter.H5Apis;
import work.soho.pay.biz.platform.wechat.adapter.JsapiApis;
import work.soho.pay.biz.platform.wechat.adapter.NativeApis;

public class FactoryApis {
    /**
     * 获取支付api接口
     *
     * @param name
     * @param payConfig
     * @return
     */
    public static Pay getApisByName(String name, PayConfig payConfig) {
        switch (name) {
            case "adapay_h5":
                return new work.soho.pay.biz.platform.adapay.adapter.WapApis(payConfig);
            case "wechat_jsapi":
                return new JsapiApis(payConfig);
            case "wechat_h5":
                return new H5Apis(payConfig);
            case "wechat_app":
                return new AppApis(payConfig);
            case "wechat_native":
                return new NativeApis(payConfig);
            case "alipay_wap":
                return new WapApis(payConfig);
            case "alipay_web":
                return new WebApis(payConfig);
            default:
                return null;
        }
    }

    public static HftCreateWallet getHftCreateWalletApisByName(String name, PayConfig payConfig) {
        switch (name) {
            case "adapay_h5":
                return new work.soho.pay.biz.platform.adapay.adapter.WapApis(payConfig);
            default:
                return null;
        }
    }
}
