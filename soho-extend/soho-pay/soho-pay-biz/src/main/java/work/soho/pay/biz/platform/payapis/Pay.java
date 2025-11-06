package work.soho.pay.biz.platform.payapis;

import work.soho.pay.biz.platform.model.Order;

import java.util.Map;

public interface Pay {
    /**
     * 支付接口
     *
     * @param order
     * @return
     */
    Map<String, String> pay(Order order) throws Exception;
}
