package work.soho.pay.biz.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.wechat.pay.java.service.payments.model.Transaction;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.pay.biz.domain.PayHfpayWallet;
import work.soho.pay.biz.domain.PayInfo;
import work.soho.pay.biz.enums.PayHfpayWalletEnums;
import work.soho.pay.biz.platform.adapay.adapter.RsaUtils;
import work.soho.pay.biz.platform.adapay.adapter.SecurityService;
import work.soho.pay.biz.platform.alipay.utils.AlipayHelpUtil;
import work.soho.pay.biz.platform.model.PayOrderDetails;
import work.soho.pay.biz.platform.wechat.model.PayOrderNotify;
import work.soho.pay.biz.platform.wechat.utils.AesUtil;
import work.soho.pay.biz.platform.wechat.utils.HelpUtil;
import work.soho.pay.biz.service.PayHfpayWalletService;
import work.soho.pay.biz.service.PayInfoService;
import work.soho.pay.biz.service.PayOrderService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

/**
 * 支付回调接口地址
 */
@Api(tags = "客户端支付回调接口")
@RequiredArgsConstructor
@RestController
@RequestMapping("/pay/guest/api/payCallback" )
@Log4j2
public class PayCallbackController {
    private final PayOrderService payOrderService;
    private final PayInfoService payInfoService;
    private final PayHfpayWalletService payHfpayWalletService;

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

    /**
     * adapay支付回调地址
     *
     * @return
     */
    @PostMapping(value = "/adapay/{id}")
    public void adapayCallback(
            @PathVariable("id") Integer id,
            @RequestParam("sign") String sign,
            @RequestParam("data") String data,
            @RequestParam("sign_type") String signType,
            HttpServletResponse response) throws IOException {
//        log.info("wechat notie request: {}", request);
        log.info("wechat callback params: {}", data);
        log.info("#callBack sign: {}",  signType);
        log.info("sign: {}", sign);
        //验签
        PayInfo payInfo = payInfoService.getById(id);
        boolean verifyResult = SecurityService.verify(data, payInfo.getAccountPublicKey(), sign);
        System.out.println("#callBack 验签结果为：" + verifyResult);

        // 验签成功
        if (verifyResult) {
            // 将返回报文中的敏感信息解密
            JSONObject respJsonData = JSONObject.parseObject(data);
            if (respJsonData != null) {
                SecurityService.getRsa2EncryptKeyList().forEach(x -> {
                    if (respJsonData.containsKey(x)) {
                        respJsonData.put(x, RsaUtils.decrypt(respJsonData.getString(x), payInfo.getAccountPrivateKey()));
                    }
                });
                System.out.println("#callBack 返回报文中的data，解密后信息为：" + respJsonData.toJSONString());

                // 成功认证， 进行支付回调处理
                PayOrderDetails payOrderDetails = new PayOrderDetails();
                payOrderDetails.setAmount(new BigDecimal(respJsonData.getString("trans_amt"))); //实际支付金额
                payOrderDetails.setPaySuccessTime(new Date());
                payOrderDetails.setOutTradeNo(respJsonData.getString("order_id"));
                payOrderDetails.setTransactionId(respJsonData.getString("platform_seq_id")); //平台单号
                payOrderDetails.setTradeType(respJsonData.getString("pay_type"));
                switch (respJsonData.getString("trans_stat")) {
                    case "S":
                        payOrderDetails.setTradeState(PayOrderDetails.TradeStateEnum.SUCCESS.getState());
                        if(payOrderService.checkPaySuccess(payOrderDetails)) {
                            response.getOutputStream().write("success".getBytes(StandardCharsets.UTF_8));
//                            return response;
                            return;
                        }
                        break;
                    case "C":
                        payOrderDetails.setTradeState(PayOrderDetails.TradeStateEnum.PAYERROR.getState());
                        break;
                }
                response.getOutputStream().write("success".getBytes(StandardCharsets.UTF_8));
            }
        } else {
            // 验签失败
            System.out.println("验签失败");
        }

        // 返回200
        response.getOutputStream().write("success".getBytes(StandardCharsets.UTF_8));
//        return response;
    }

    /**
     * 钱包开户回调接口
     *
     * @param id
     * @param sign
     * @param data
     * @param signType
     * @param response
     * @throws IOException
     */
    @PostMapping(value = "/adapay-wallet/{id}")
    public void adapayWalletCallback(
            @PathVariable("id") Integer id,
            @RequestParam("sign") String sign,
            @RequestParam("data") String data,
            @RequestParam("sign_type") String signType,
            HttpServletResponse response) throws IOException {
        log.info("wechat callback params: {}", data);
        log.info("#callBack sign: {}",  signType);
        log.info("sign: {}", sign);
        //验签
        PayInfo payInfo = payInfoService.getById(id);

        // 待验签内容：{"acct_id":"B00059982","bind_card_id":"","cmd_id":"w00003","extension":"","fee_acct_id":"","fee_amt":"","fee_cust_id":"","id_card":"SzroF9p5mQWdQGbHmoxwAvnlu99S4SQPwIzySQcx/k5etQqRrMJZpH1PLJt/IMPogKRZhvugKl7i3gsjigVqU+VbaRBNnvsTq0knW/jc2ZdHeVu7NzMRj0B+SnSMomc1atKFJbz9VMnOZvErEvA/IbtdDql9NxDHhulXURoX0/h0jR5befckel1QsKZOw4A7GLioxBHnJ3qAgEIgzvhXZ8U7XSkizjzSrj9xCZRZXLqHgRK4SRRfpuE1yNfDFQiDSfjp6o/u75M/RyHKHrh4CbZSVeccvRpZ1lKAsUZEzi45Xf8owZxWnsQ0Cd6fsC1Xtvv+vxxJbZ4g0I0N2NrG/A==","id_card_type":"10","mer_cust_id":"6666000100062504","mer_priv":"","order_date":"20250423","order_id":"427557217219121152","platform_seq_id":"20250423572821384362305095","resp_code":"C00000","resp_desc":"成功","user_cust_id":"6666000100062649","user_id":"6","user_mobile":"buhvSoFzuMPWinZAvUu2l/PjLAYrg6B93/icNYDu0GVfu763BA7/CRCIcEhL/7MfNQI6vxwhyj2zc7akkZjWoL+LsWQR4P8APWmkSge8AMdYi9Ou5Ux8ehd6EurlrzOjbwtJfaX3JYmnYwWkh1QZ99ok/Y9cotdU5mjK+jICaLOGkG4Kw98mnGTTnuWUXJwstz3aQViiubLQ/moLJgc7T4emgKFPcmzLSekRk14x8P1YkdxsE8w0MzcL+9J/nS19vnWVMZgsIWXl942FqiwIwdi241qx0DFUbSPcRWq7/mDdQ1uf3J8tReam9SfTuVz1mxsjPZeFDgzQ1IHNWygWQg==","user_name":"黄文杰"}
        boolean verifyResult = SecurityService.verify(data, payInfo.getAccountPublicKey(), sign);
        System.out.println("#callBack 验签结果为：" + verifyResult);

        // 验签成功
        if (verifyResult) {
            // 将返回报文中的敏感信息解密
            JSONObject respJsonData = JSONObject.parseObject(data);
            // TODO 执行钱包相关业务逻辑
            PayHfpayWallet payHfpayWallet = new PayHfpayWallet();
            payHfpayWallet.setStatus(PayHfpayWalletEnums.Status.SUCCESS.getCode());
            payHfpayWallet.setPayInfoId(id);
            payHfpayWallet.setOrderCode(IDGeneratorUtils.snowflake().toString());
            payHfpayWallet.setOrderCode(respJsonData.getString("order_id"));
            payHfpayWallet.setUserId(respJsonData.getLongValue("user_id"));
            payHfpayWallet.setUserCustId(respJsonData.getString("user_cust_id"));
            payHfpayWallet.setAcctId(respJsonData.getString("acct_id"));
            payHfpayWallet.setFeeCustId(respJsonData.getString("fee_cust_id"));
            payHfpayWallet.setFeeAcctId(respJsonData.getString("fee_acct_id"));
            payHfpayWallet.setFeeAmt(respJsonData.getString("fee_amt"));
            payHfpayWallet.setIdCard(respJsonData.getString("Id_card"));
            payHfpayWallet.setIdCardType(respJsonData.getString("id_card_type"));
            payHfpayWallet.setUserName(respJsonData.getString("user_name"));
            payHfpayWallet.setUserMobile(respJsonData.getString("user_mobile"));
            payHfpayWallet.setCreatedTime(LocalDateTime.now());
            payHfpayWallet.setUpdatedTime(LocalDateTime.now());
            payHfpayWalletService.save(payHfpayWallet);
        }
    }
}
