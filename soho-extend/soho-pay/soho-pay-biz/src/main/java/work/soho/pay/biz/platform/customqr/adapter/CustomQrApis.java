package work.soho.pay.biz.platform.customqr.adapter;

import work.soho.common.core.util.StringUtils;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.platform.payapis.Pay;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义二维码收款支付适配器。
 */
public class CustomQrApis implements Pay {
    private final PayConfig payConfig;

    /**
     * 构造自定义二维码支付适配器。
     *
     * @param payConfig 支付配置
     */
    public CustomQrApis(PayConfig payConfig) {
        this.payConfig = payConfig;
    }

    /**
     * 创建支付参数。
     * 返回收款码和展示字段，前端完成支付后调用上报接口。
     *
     * @param order 支付订单
     * @return 支付参数
     * @throws Exception 创建异常
     */
    @Override
    public Map<String, String> pay(Order order) throws Exception {
        HashMap<String, String> result = new HashMap<>();
        result.put("pay_type", "custom_qr");
        result.put("report_required", "true");
        result.put("order_no", order.getOutTradeNo());
        result.put("amount", order.getAmount() == null ? "" : order.getAmount().toPlainString());
        result.put("description", StringUtils.isBlank(order.getDescription()) ? "" : order.getDescription());
        result.put("pay_info_id", payConfig.getId() == null ? "" : payConfig.getId().toString());
        result.put("pay_title", StringUtils.isBlank(payConfig.getTitle()) ? "" : payConfig.getTitle());
        result.put("pay_account_name", StringUtils.isBlank(payConfig.getAccountName()) ? "" : payConfig.getAccountName());
        result.put("pay_qr_image", StringUtils.isBlank(payConfig.getAccountPublicKey()) ? "" : payConfig.getAccountPublicKey());
        return result;
    }
}
