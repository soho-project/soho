package work.soho.pay.biz.platform.customqr.adapter;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.BeansException;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.core.util.StringUtils;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.platform.payapis.Pay;
import work.soho.pay.biz.service.PayManualOrderPollNotifier;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
        result.put("pay_qr_image", resolvePayQrImage(order));
        notifyNewPayOrder(order);
        return result;
    }

    /**
     * 解析实际应返回的收款二维码内容。
     *
     * 支持两种配置格式：
     * 1. 直接保存二维码字符串，兼容旧配置。
     * 2. JSON 配置，优先按金额命中 fixed_amount，未命中时回退 any_amount。
     *
     * @param order 支付订单
     * @return 收款二维码内容
     */
    private String resolvePayQrImage(Order order) {
        String qrConfig = payConfig.getAccountPublicKey();
        if (StringUtils.isBlank(qrConfig)) {
            return "";
        }
        if (!JSONUtil.isTypeJSON(qrConfig)) {
            return qrConfig;
        }

        try {
            JSONObject jsonObject = JSONUtil.parseObj(qrConfig);
            String fixedAmountQr = resolveFixedAmountQr(jsonObject, order == null ? null : order.getAmount());
            if (StringUtils.isNotBlank(fixedAmountQr)) {
                return fixedAmountQr;
            }
            return jsonObject.getStr("any_amount", "");
        } catch (Exception ex) {
            // 配置异常时回退原始值，避免影响支付下单流程。
            return qrConfig;
        }
    }

    /**
     * 按订单金额优先匹配固定金额二维码。
     *
     * @param jsonObject 二维码配置 JSON
     * @param amount 支付金额
     * @return 固定金额二维码，未命中时返回空字符串
     */
    private String resolveFixedAmountQr(JSONObject jsonObject, BigDecimal amount) {
        if (jsonObject == null || amount == null || !jsonObject.containsKey("fixed_amount")) {
            return "";
        }
        JSONObject fixedAmountObject = jsonObject.getJSONObject("fixed_amount");
        if (fixedAmountObject == null || fixedAmountObject.isEmpty()) {
            return "";
        }

        for (String amountKey : buildAmountMatchKeys(amount)) {
            String qrCode = fixedAmountObject.getStr(amountKey);
            if (StringUtils.isNotBlank(qrCode)) {
                return qrCode;
            }
        }
        return "";
    }

    /**
     * 构建金额匹配候选 key。
     *
     * 兼容配置中使用 `100`、`100.0`、`100.00` 等不同格式。
     *
     * @param amount 支付金额
     * @return 候选 key 集合
     */
    private Set<String> buildAmountMatchKeys(BigDecimal amount) {
        LinkedHashSet<String> keys = new LinkedHashSet<>();
        if (amount == null) {
            return keys;
        }

        keys.add(amount.toPlainString());
        keys.add(amount.stripTrailingZeros().toPlainString());
        if (amount.scale() < 2) {
            keys.add(amount.setScale(2, RoundingMode.UNNECESSARY).toPlainString());
        }
        return keys;
    }

    /**
     * 通知轮询接口有新支付单创建。
     *
     * @param order 支付订单
     */
    private void notifyNewPayOrder(Order order) {
        if (payConfig.getId() == null || order == null || StringUtils.isBlank(order.getOutTradeNo())) {
            return;
        }
        try {
            PayManualOrderPollNotifier notifier = SpringContextHolder.getBean(PayManualOrderPollNotifier.class);
            notifier.notifyNewOrder(payConfig.getId(), order.getOutTradeNo());
        } catch (BeansException ex) {
            // 支付下单流程不能因为通知失败而中断
        }
    }
}
