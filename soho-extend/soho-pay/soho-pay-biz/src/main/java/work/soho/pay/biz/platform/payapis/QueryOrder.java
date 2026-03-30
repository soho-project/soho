package work.soho.pay.biz.platform.payapis;

import work.soho.pay.biz.platform.model.PayOrderDetails;

public interface QueryOrder {
    /**
     * 查询订单
     *
     * @param outTradeNo 本地支付单号
     * @return 支付订单详情
     */
    PayOrderDetails queryOrder(String outTradeNo);
}
