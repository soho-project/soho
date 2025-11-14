package work.soho.pay.biz.platform.wallet.adapter;

import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.platform.payapis.Pay;

import java.util.HashMap;
import java.util.Map;

public class WalletApis implements Pay {
    private PayConfig payConfig;

    public WalletApis(PayConfig payConfig) {
        this.payConfig = payConfig;
    }

    /**
     * 钱包支付
     *
     * nothing todo
     *
     * 具体支付逻辑在钱包实现； 结果通知在监听器实现支付单状态同步
     *
     * @param order
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, String> pay(Order order) throws Exception {
        // 返回钱包类型
        HashMap<String, String> result = new HashMap<>();
        result.put("pay_type", "wallet");
        // 返回钱包类型
        result.put("wallet_type", payConfig.getAppId());
        return result;
    }
}
