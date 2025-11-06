package work.soho.pay.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.RequestUtil;
import work.soho.pay.api.dto.OrderDetailsDto;
import work.soho.pay.api.event.PayCallbackEvent;
import work.soho.pay.api.service.PayOrderApiService;
import work.soho.pay.biz.domain.PayHfpayWallet;
import work.soho.pay.biz.domain.PayInfo;
import work.soho.pay.biz.domain.PayOrder;
import work.soho.pay.biz.enums.PayHfpayWalletEnums;
import work.soho.pay.biz.mapper.PayHfpayWalletMapper;
import work.soho.pay.biz.mapper.PayOrderMapper;
import work.soho.pay.biz.platform.FactoryApis;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.platform.model.PayOrderDetails;
import work.soho.pay.biz.platform.payapis.Pay;
import work.soho.pay.biz.service.PayInfoService;
import work.soho.pay.biz.service.PayOrderService;

import java.time.LocalDateTime;
import java.util.Map;

/**
* @author i
* @description 针对表【pay_order(支付单)】的数据库操作Service实现
* @createDate 2022-11-11 16:31:43
*/
@RequiredArgsConstructor
@Service
@Log4j2
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder>
    implements PayOrderService, PayOrderApiService {

    private final PayInfoService payInfoService;
    private final PayHfpayWalletMapper payHfpayWalletMapper;

    @Override
    public Map<String, String> payOrder(OrderDetailsDto orderDetailsDto) {
        return pay(orderDetailsDto);
    }

    /**
     * 支付调用接口
     *
     * @param orderDetailsDto
     * @return
     */
    @Override
    public Map<String, String> pay(OrderDetailsDto orderDetailsDto) {
        try {
            //获取支付方案
            PayInfo payInfo = payInfoService.getById(orderDetailsDto.getPayInfoId());
            PayConfig payConfig = new PayConfig();
            payConfig.setId(payInfo.getId());
            payConfig.setPayCertificate(payInfo.getAccountPublicKey());
            payConfig.setMerchantSerialNumber(payInfo.getAccountSerialNumber());
            payConfig.setPrivateKey(payInfo.getAccountPrivateKey());
            payConfig.setMerchantId(payInfo.getAccountId());
            payConfig.setAppId(payInfo.getAccountAppId());
            //获取支付驱动
            Pay payApis = FactoryApis.getApisByName(payInfo.getAdapterName(), payConfig);
            //创建请求支付单
            Order order = BeanUtils.copy(orderDetailsDto, Order.class);
            //TODO 设置回调地址; 本机的回调地址
            String callbackUrl = getCallbackUrl(payInfo.getAdapterName());
            log.info(callbackUrl);
            order.setNotifyUrl(callbackUrl);
            //创建支付单入库
            PayOrder payOrder = new PayOrder();
            payOrder.setPayId(payInfo.getId());
            payOrder.setOrderNo(IDGeneratorUtils.snowflake().toString());
            payOrder.setUpdatedTime(LocalDateTime.now());
            payOrder.setCreatedTime(LocalDateTime.now());
            payOrder.setStatus(PayOrderDetails.TradeStateEnum.NOTPAY.getState());
            payOrder.setAmount(orderDetailsDto.getAmount());
            payOrder.setTrackingNo(orderDetailsDto.getOutTradeNo());
            payOrder.setNotifyUrl(orderDetailsDto.getNotifyUrl());
            payOrder.setUserCustId(getUserCustId(orderDetailsDto.getUserId(), payInfo));
            save(payOrder);

            order.setUserCustId(payOrder.getUserCustId());
            order.setOutTradeNo(payOrder.getOrderNo());
            return payApis.pay(order);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        return null;
    }

    /**
     * 获取指定用户的支付客户ID
     *
     * @param userId
     * @param payInfo
     * @return
     */
    private String getUserCustId(Long userId, PayInfo payInfo) {
        try {
            if(payInfo.getAdapterName().equals("adapay_h5")) {
                LambdaQueryWrapper<PayHfpayWallet> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(PayHfpayWallet::getUserId, userId);
                lambdaQueryWrapper.eq(PayHfpayWallet::getPayInfoId, payInfo.getId());
                lambdaQueryWrapper.eq(PayHfpayWallet::getStatus, PayHfpayWalletEnums.Status.SUCCESS.getCode());
                //获取用户钱包
                PayHfpayWallet payHfpayWallet = payHfpayWalletMapper.selectOne(lambdaQueryWrapper);
                if(payHfpayWallet != null) {
                    return payHfpayWallet.getUserCustId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取回调地址
     *
     * TODO 可改为后台固定前缀配置地址
     *
     * @param adapterName
     * @return
     */
    private String getCallbackUrl(String adapterName) {
        String[] parts = adapterName.split("_");
        if(parts.length<1) {
            throw new RuntimeException("请联系管理员配置回调地址");
        }
        return RequestUtil.getRequest().getScheme() + "://" + RequestUtil.getRequest().getHeader("HOST")
                + "/client/api/payCallback/"+parts[0];
    }

    /**
     * 检查处理支付成功支付单
     *
     * @param payOrderDetails
     * @return
     */
    @Override
    public Boolean checkPaySuccess(PayOrderDetails payOrderDetails) {
        if(payOrderDetails.getTradeState().intValue() != PayOrderDetails.TradeStateEnum.SUCCESS.getState()) {
            return Boolean.FALSE;
        }

        //支付成功，处理相关业务
        LambdaQueryWrapper<PayOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PayOrder::getOrderNo, payOrderDetails.getOutTradeNo());
        lambdaQueryWrapper.ne(PayOrder::getStatus, PayOrderDetails.TradeStateEnum.CLOSED.getState());
        PayOrder payOrder =  getOne(lambdaQueryWrapper);
        payOrder.setStatus(PayOrderDetails.TradeStateEnum.SUCCESS.getState());
        payOrder.setTransactionId(payOrderDetails.getTransactionId());
        payOrder.setPayedTime(LocalDateTime.now());
        payOrder.setUpdatedTime(LocalDateTime.now());
        updateById(payOrder);
        //支付事件通知
        PayCallbackEvent payCallbackEvent = new PayCallbackEvent();
        payCallbackEvent.setStatus(payOrder.getStatus());
        payCallbackEvent.setOrderNo(payOrder.getOrderNo());
        payCallbackEvent.setOutTradeNo(payOrder.getTrackingNo());
        payCallbackEvent.setTransactionNo(payOrder.getTransactionId());
        payCallbackEvent.setPayInfoId(payOrder.getPayId());
        SpringContextHolder.getApplicationContext().publishEvent(payCallbackEvent);
        return Boolean.TRUE;
    }
}




