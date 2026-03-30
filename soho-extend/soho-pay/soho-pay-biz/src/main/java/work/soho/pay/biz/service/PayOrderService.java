package work.soho.pay.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.pay.api.dto.CreatePayInfoDto;
import work.soho.pay.api.dto.OrderDetailsDto;
import work.soho.pay.biz.domain.PayOrder;
import work.soho.pay.biz.platform.model.PayOrderDetails;

import java.util.Map;

/**
* @author i
* @description 针对表【pay_order(支付单)】的数据库操作Service
* @createDate 2022-11-11 16:31:43
*/
public interface PayOrderService extends IService<PayOrder> {
    CreatePayInfoDto pay(OrderDetailsDto orderDetailsDto);
    Boolean checkPaySuccess(PayOrderDetails payOrderDetails);

    /**
     * 主动同步订单支付状态（供客户端轮询）
     *
     * @param orderNo 本地支付单号
     * @return 同步结果
     */
    Map<String, Object> syncOrderState(String orderNo);
}
