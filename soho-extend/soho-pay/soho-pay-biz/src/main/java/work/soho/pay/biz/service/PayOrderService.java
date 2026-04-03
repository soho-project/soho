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
    /**
     * 创建支付单并生成支付参数。
     *
     * @param orderDetailsDto 下单参数
     * @return 支付参数结果
     */
    CreatePayInfoDto pay(OrderDetailsDto orderDetailsDto);

    /**
     * 处理支付网关回调并检查是否支付成功。
     *
     * @param payOrderDetails 网关订单详情
     * @return 是否处理成功
     */
    Boolean checkPaySuccess(PayOrderDetails payOrderDetails);

    /**
     * 主动同步订单支付状态（供客户端轮询）
     *
     * @param orderNo 本地支付单号
     * @return 同步结果
     */
    Map<String, Object> syncOrderState(String orderNo);

    /**
     * 人工上报场景下将订单确认支付成功。
     *
     * @param orderNo 支付单号
     * @param transactionId 三方交易号
     * @param payedTime 支付时间
     * @return 是否确认成功
     */
    Boolean confirmOrderPaid(String orderNo, String transactionId, java.time.LocalDateTime payedTime);

}
