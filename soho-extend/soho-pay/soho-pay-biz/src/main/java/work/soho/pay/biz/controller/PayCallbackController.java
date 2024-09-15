package work.soho.pay.biz.controller;

import cn.hutool.json.JSONUtil;
import com.alipay.api.internal.util.AlipaySignature;
import com.wechat.pay.java.service.payments.model.Transaction;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import work.soho.pay.biz.platform.alipay.utils.AlipayHelpUtil;
import work.soho.pay.biz.platform.model.PayOrderDetails;
import work.soho.pay.biz.platform.wechat.utils.AesUtil;
import work.soho.pay.biz.platform.wechat.model.PayOrderNotify;
import work.soho.pay.biz.platform.wechat.utils.HelpUtil;
import work.soho.pay.biz.service.PayOrderService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;

/**
 * 支付回调接口地址
 */
@Api(tags = "客户端支付回调接口")
@Controller
@RequiredArgsConstructor
@RequestMapping("/pay/client/api/payCallback" )
@Log4j2
public class PayCallbackController {
    private final PayOrderService payOrderService;

    /**
     * 微信支付回调接口
     *
     * 处理成功返回 HTTP应答状态码需返回200或204，无需返回应答报文。
     * 处理失败返回 HTTP应答状态码需返回5XX或4XX
     *
     * @return
     */
    @PostMapping("wechat")
    public HttpServletResponse wechat(@RequestBody PayOrderNotify payOrderNotify,HttpServletResponse request,
                                      HttpServletResponse response) throws GeneralSecurityException, IOException {
        log.info("wechat notie request: {}", request);
        log.info("wechat callback params: {}", payOrderNotify);

        String sign = request.getHeader("Wechatpay-Signature");
        //TODO 签名校验

        AesUtil aesUtil = new AesUtil("bb389f39453abe2218695d2f451bb957".getBytes(StandardCharsets.UTF_8));
        String body = aesUtil.decryptToString(payOrderNotify.getResource().getAssociatedData().getBytes(StandardCharsets.UTF_8),
                payOrderNotify.getResource().getNonce().getBytes(StandardCharsets.UTF_8),payOrderNotify.getResource().getCiphertext());

        Transaction transaction = JSONUtil.toBean(body, Transaction.class);
        PayOrderDetails payOrderDetails = HelpUtil.fromTransaction(transaction);

        //对支付结果进行处理
        if(payOrderDetails.getTradeState().intValue() == PayOrderDetails.TradeStateEnum.SUCCESS.getState()) {
            //订单支付成功
            //TODO 更新微信业务单号到数据库， 更新数据库支付状态
            if(payOrderService.checkPaySuccess(payOrderDetails)) {
                response.setStatus(200);
                return response;
            }
        }
        response.setStatus(500);
        return response;
    }

    /**
     * 阿里云支付回调地址
     *
     * @return
     */
    @PostMapping("alipay")
    public HttpServletResponse alipay(@RequestBody HashMap<String, String> params,
                                      HttpServletResponse response, HttpServletRequest request) throws Exception {
        log.info("wechat notie request: {}", request);
        log.info("wechat callback params: {}", params);
        if(AlipaySignature.rsaCheckV1(params, "", StandardCharsets.UTF_8.name())) {
            PayOrderDetails payOrderDetails = AlipayHelpUtil.fromHashMap(params);
            if(payOrderDetails.getTradeState() != PayOrderDetails.TradeStateEnum.SUCCESS.getState()) {
                throw new Exception("未支付");
            }
            if(payOrderService.checkPaySuccess(payOrderDetails)) {
                //更新订单相关信息
                response.getOutputStream().write("success".getBytes(StandardCharsets.UTF_8));
                return response;
            }
        }
        response.getOutputStream().write("fail".getBytes(StandardCharsets.UTF_8));
        return response;
    }
}
