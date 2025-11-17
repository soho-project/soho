package work.soho.pay.biz.platform.wallet.adapter;

import work.soho.common.core.support.SpringContextHolder;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.platform.payapis.Pay;
import work.soho.wallet.api.dto.CreateUserOrderDTO;
import work.soho.wallet.api.service.WalletUserOrderApiService;

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
        WalletUserOrderApiService walletUserOrderApiService = SpringContextHolder.getBean(WalletUserOrderApiService.class);
        CreateUserOrderDTO createUserOrderDTO = walletUserOrderApiService.createPayOrder(new CreateUserOrderDTO()
                .setNo(order.getOutTradeNo())
                .setWalletTypeName(payConfig.getAppId())
                .setAmount(order.getAmount())
                .setOutTrackingNumber(order.getOutTradeNo())
                .setStatus(0)
                .setNotes("订单： " + order.getOutTradeNo())
        );
        // 返回钱包类型
        HashMap<String, String> result = new HashMap<>();
        result.put("pay_type", "wallet");
        // 返回钱包类型
        result.put("wallet_type", payConfig.getAppId());
        result.put("pay_order_id", createUserOrderDTO.getId().toString());
        result.put("pay_order_no", createUserOrderDTO.getNo());
        return result;
    }
}
