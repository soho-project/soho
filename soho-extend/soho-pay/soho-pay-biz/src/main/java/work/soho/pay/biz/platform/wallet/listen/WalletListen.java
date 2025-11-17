package work.soho.pay.biz.platform.wallet.listen;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import work.soho.pay.biz.domain.PayOrder;
import work.soho.pay.biz.platform.model.PayOrderDetails;
import work.soho.pay.biz.service.PayOrderService;
import work.soho.wallet.api.event.WalletUserOrderEvent;

import java.util.Date;

/**
 * 钱包支付监听
 */
@Component
@RequiredArgsConstructor
public class WalletListen {

    private final PayOrderService payOrderService;

    @EventListener(WalletUserOrderEvent.class)
    public void onPaySuccess(WalletUserOrderEvent  event) {
        PayOrder payOrder = payOrderService.getOne(new LambdaQueryWrapper<PayOrder>()
            .eq(PayOrder::getOrderNo, event.getOutTrackingNumber()));

        if(payOrder != null) {
            PayOrderDetails payOrderDetails = new PayOrderDetails();
            payOrderDetails.setTransactionId(event.getNo());
            payOrderDetails.setOutTradeNo(event.getOutTrackingNumber());
            payOrderDetails.setAmount(event.getAmount());
            payOrderDetails.setPaySuccessTime(new Date());
            payOrderDetails.setTradeState(PayOrderDetails.TradeStateEnum.SUCCESS.getState());

            payOrderService.checkPaySuccess(payOrderDetails);
        }

    }
}