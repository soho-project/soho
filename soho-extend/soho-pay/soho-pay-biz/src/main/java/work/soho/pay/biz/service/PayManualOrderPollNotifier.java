package work.soho.pay.biz.service;

/**
 * 自定义二维码支付单长轮询通知器。
 */
public interface PayManualOrderPollNotifier {
    /**
     * 通知指定支付方式有新的支付单创建。
     *
     * @param payInfoId 支付方式 ID
     * @param orderNo 支付单号
     */
    void notifyNewOrder(Integer payInfoId, String orderNo);

    /**
     * 阻塞等待指定支付方式的新支付单通知。
     *
     * @param payInfoId 支付方式 ID
     * @param waitSeconds 等待秒数
     * @return 是否收到通知
     */
    boolean awaitNewOrder(Integer payInfoId, int waitSeconds);
}
