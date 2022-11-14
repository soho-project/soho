package work.soho.pay.biz.platform.payapis;

public interface CloseOrder {
    /**
     * 关闭订单
     *
     * @param outTrackNo
     */
    void closeOrder(String outTrackNo);
}
