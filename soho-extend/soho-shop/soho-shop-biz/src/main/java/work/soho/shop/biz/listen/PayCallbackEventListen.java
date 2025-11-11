package work.soho.shop.biz.listen;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import work.soho.pay.api.event.PayCallbackEvent;
import work.soho.shop.biz.domain.ShopOrderInfo;
import work.soho.shop.biz.enums.ShopOrderInfoEnums;
import work.soho.shop.biz.service.ShopOrderInfoService;

@Service
@RequiredArgsConstructor
public class PayCallbackEventListen {
    private final ShopOrderInfoService shopOrderInfoService;

    /**
     * 支付回调
     */
    public void payCallback(PayCallbackEvent payCallbackEvent) {
        // 订单支付成功
        if(payCallbackEvent.getStatus() != PayCallbackEvent.PayOrderStatus.SUCCESS.getId()) {
            return;
        }
//        System.out.println("支付成功", payCallbackEvent);
        ShopOrderInfo shopOrderInfo = shopOrderInfoService.getById(payCallbackEvent.getOrderNo());
        // 修改订单支付状态
        shopOrderInfo.setPayStatus(ShopOrderInfoEnums.PayStatus.PAID.getId());
        shopOrderInfoService.updateById(shopOrderInfo);

        //虚拟订单进行相关业务处理
        onPaid(payCallbackEvent);
    }

    /**
     * 支付成功
     *
     * TODO 有些订单需要在支付成功的时候进行业务处理； 例如： 虚拟订单，服务订单等
     *
     * @param payCallbackEvent
     */
    @Async
    public void onPaid(PayCallbackEvent payCallbackEvent) {
//        System.out.println("支付成功", payCallbackEvent);
    }
}
