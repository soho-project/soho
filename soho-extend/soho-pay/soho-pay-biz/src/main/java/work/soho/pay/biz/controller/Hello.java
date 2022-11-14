package work.soho.pay.biz.controller;

import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.wechat.pay.java.core.util.PemUtil;

//import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest as a;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.pay.api.dto.OrderDetailsDto;
import work.soho.pay.biz.platform.alipay.adapter.WebApis;
import work.soho.pay.biz.platform.model.PayOrderDetails;
import work.soho.pay.biz.platform.PayConfig;
import work.soho.pay.biz.platform.wechat.adapter.H5Apis;
import work.soho.pay.biz.platform.wechat.adapter.NativeApis;
import work.soho.pay.biz.platform.model.Order;
import work.soho.pay.biz.service.PayOrderService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/client/api/hello" )
public class Hello {
    private final PayOrderService payOrderService;

    @GetMapping("hello")
    public String hello() {
        /** 商户号 */
        String merchantId = "1634198700";
        /** 商户API私钥路径 */
        String privateKeyPath = "D:\\work\\docs\\jht\\支付\\1634198700_20221109_cert\\apiclient_key.pem";
        /** 商户证书序列号 */
        String merchantSerialNumber = "7BC2055A5F8359FF53C4A4BAD88A505A3376DBF3";
//        String merchantSerialNumber = "7BC2055A5F8359FF53C4A4BAD88A505A3376DBF3";
//        String merchantSerialNumber = "5B4161F025D87244156DDEA7264B107F996E0EBD";
        /** 微信支付平台证书路径 */
//        String wechatPayCertificatePath = "D:\\work\\docs\\jht\\支付\\1634198700_20221109_cert\\apiclient_cert.pem";
        String wechatPayCertificatePath = "D:\\work\\docs\\jht\\支付\\1634198700_20221109_cert\\wechat\\wechatpay_5B4161F025D87244156DDEA7264B107F996E0EBD.pem";
        /** 微信支付 APIv3 密钥 */
        String apiV3Key = "bb389f39453abe2218695d2f451bb957";

        //微信openid fang
        String openId = "oDo-_6mpN998CPd-X9dmVc0lLDas";
        //app id
        String appId = "wx305fe28de326ebb0";

        String privateKeyString = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDiGZagR+8Kb0WM\n" +
                "PWGftq/ihaO+C7yswWBcJkvjsb6bNiA31SpV7knbmRf925FZPoyV47YbQyTgfIMQ\n" +
                "HZDLrygPF9FXCtz6iEd3UBjcZorab4VR7mJyxEkEmDxazc9CLLtw8TBnYbjj0wp1\n" +
                "wXaukTh8gkq4qkaFqv628QvD/kTNXHV6EqG8G/TmXEnBZoKXCsPeM0QthXsLOu8W\n" +
                "00cJ6blfcdz3pxq2sGoSPLwS9Xv3y3DPWh/hoWPRlIEMarQgedGb2Aza2kbVsDN2\n" +
                "WjiiAZSLwf85PT57usGSlzcaQc1chmX2TaMjKEXYBiHr+F+LY4SuCMQ8pvzM48Gg\n" +
                "/MNxy7M1AgMBAAECggEAcUP+WDcRAfoyLgoF4NvFCsaF+8unbifFnQQ2Wk+Nu/WG\n" +
                "eFHaTof8Cf2MrkiTbNDP3rrfXJLrY2YcxjL5eYA6oiz9Wwx/HSauHpBKlAbuonTh\n" +
                "peS0/udj6OBWw+p8XJtKH09EP1YOKFttzwXm7ZakZINk6VOr1oSN65mfmkTDtNge\n" +
                "c4JZgc4YG39Z24F0iTHyGctHqDW3vq4dXus9DJSQHpf8u8ituSqpKkmpkyxTpRQa\n" +
                "uoay8Ac9re20N9R2lYn/RP1+qD/tl2YrmbkruIKRd1aLYvESYLkovB7sLIvSLr0J\n" +
                "SiRYhIxqeV9z8iFBA0Y8snE7/+jpgxJycL5DkCAAFQKBgQD5Kv0l/rC1TWRyQRnG\n" +
                "lu0E47Vk466Uu83dRj1IP4K0vyfhF7lJIO7kQe0CFsRo5zNZHKkqKKwGTVtFlbvo\n" +
                "3w45vkhyYtl3f3LEGcCxRc6TVzHwwa0GvXQqE/7o8n/98iNbOMZu654stBaZbnnl\n" +
                "celujAK03a0S/L6tm2u9zF+PqwKBgQDoTK0RGZitabwsKOsd9pl3fjJJlopmDcGs\n" +
                "EphJueHsu7xC+WR2LQ5JXipAtDayqTt3rDFNoD0sweIeDBfksuSUsyTzqpy66aNd\n" +
                "McETAhrFLkse8nrNpOgBIgqLVrS2Z5P8mCJWits5KphmvPDZhXRw9/Tm7pdndFZY\n" +
                "v1wrDDxonwKBgQCxSdQ7v5+uMPYCka2sbuEQtJDxy0sCCyrsgbjC+mWfA9B1SbMN\n" +
                "hIBWK05fSSHRXohbpJf9JllYVpxVw7ejd2qgzXKw6QLBEw7u10ATIo9cqRMgsj3R\n" +
                "6OWJtxaOW1WUtEBR/PC8JawzNKRL4Z6cElQ80yRt2rj2JJ3r2wVrU8q+5wKBgFnc\n" +
                "j3Z1+GHc7TYH/ivxIRzgbIObAJ7J44m1B8Q9a/AwD3u5DevJMiWfj6jIDgC/Booc\n" +
                "ylXFDuUpe1c0Rmnp6hK72ieIcGi9yLxcatv3jHOKnPSzyF2U1Ura6ElOmUmWd+DW\n" +
                "2TGxNWy/1YSowmLjBVBOGybaVSdoEZ7zJ6xfZ17xAoGBANk2ar8R/0/ShUlZhjO3\n" +
                "zmFC0CoDYQktYV4x3VYn1lN8L5MRksL4084A3csNSWUkKSGpFGZ8FUQfsSt1l7Mg\n" +
                "4+Djml+Xp1Zn9PMi85suz3vMGsMt9oo5HlxwCc1VViONYoIY6bcz7Q6j33Sy1Pbb\n" +
                "6z3Dxwohx9FC2sinWkWuXwhj\n" +
                "-----END PRIVATE KEY-----\n";

        String wechatPayCertificateString = "-----BEGIN CERTIFICATE-----\n" +
                "MIID3DCCAsSgAwIBAgIUW0Fh8CXYckQVbd6nJksQf5luDr0wDQYJKoZIhvcNAQEL\n" +
                "BQAwXjELMAkGA1UEBhMCQ04xEzARBgNVBAoTClRlbnBheS5jb20xHTAbBgNVBAsT\n" +
                "FFRlbnBheS5jb20gQ0EgQ2VudGVyMRswGQYDVQQDExJUZW5wYXkuY29tIFJvb3Qg\n" +
                "Q0EwHhcNMjIxMTA5MDY1NDIyWhcNMjcxMTA4MDY1NDIyWjBuMRgwFgYDVQQDDA9U\n" +
                "ZW5wYXkuY29tIHNpZ24xEzARBgNVBAoMClRlbnBheS5jb20xHTAbBgNVBAsMFFRl\n" +
                "bnBheS5jb20gQ0EgQ2VudGVyMQswCQYDVQQGDAJDTjERMA8GA1UEBwwIU2hlblpo\n" +
                "ZW4wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC8rtClzmJm4lK9ywQS\n" +
                "tG1TGIPeIPed8sSgPFC1txzF5dX613BtzVyTsz2mEGw+G4rjufnMuHNvblzR0L9/\n" +
                "WiNSkzghlFKxlYes/GaAf8MFjKyd7bHPt/RKEEvp0aViXcfKPT2hx3hhidWWCYVQ\n" +
                "Zj0rVkYJGijQBmHrQG2y/MGDT75OrH0wZImP6n+PD7XnAjTTixqJYtSmQqiqmcW7\n" +
                "CBrP83qGytCoXj56CeqvXsgg2ZS9h/HgsXDpm9BC2UhmaJiZdIHcj42VUJesrqQn\n" +
                "5Y5eYLmQuVqQtj5/nHKBxLVhebb97/h7oGzenJUWPeV0VzMYdSJqTdoFlfUaOKLe\n" +
                "Ow+PAgMBAAGjgYEwfzAJBgNVHRMEAjAAMAsGA1UdDwQEAwID+DBlBgNVHR8EXjBc\n" +
                "MFqgWKBWhlRodHRwOi8vZXZjYS5pdHJ1cy5jb20uY24vcHVibGljL2l0cnVzY3Js\n" +
                "P0NBPTFCRDQyMjBFNTBEQkMwNEIwNkFEMzk3NTQ5ODQ2QzAxQzNFOEVCRDIwDQYJ\n" +
                "KoZIhvcNAQELBQADggEBAGn26KuOaUwOW89JfWZxfT7F8mTpTJ5Y1j/EKSHrtx/T\n" +
                "j1F8FBkHWLgwKvalAcpwUlRgCk185FM1F3B1Me5gOXwPQOKyTx5DgyGRMWoVf+Q6\n" +
                "bg4S+XP1R5chlBCg5Tv68tcbrwoC34L7tQpBL7aqQ0S9dDnfqpIvb93WBnRiFVjY\n" +
                "wfBbZlDedAanGwornxSbIBgiyOtglo9n9Fk0koU2nVGCgoaWCedsptzH+P9fkE+Q\n" +
                "LkbgYbwC86Yx3K/o2jBTjrAnMplMPvhi8UMeun4kqbgBMKp0Y+H8uwMfUqj+CsWC\n" +
                "fbgMAh5v309A6xsyyUkakhZrXFF4Yo7z5ANlcBXlJFk=\n" +
                "-----END CERTIFICATE-----";


//        Config config =
//                new RSAConfig.Builder()
//                        .merchantId(merchantId)
//                        .privateKeyFromPath(privateKeyPath)
//                        .merchantSerialNumber(merchantSerialNumber)
//                        .wechatPayCertificatesFromPath(wechatPayCertificatePath)
//                        .build();

        PayConfig payConfig = new PayConfig();
//        payConfig.setConfig(config);
//        payConfig.setOpenId(openId);
        payConfig.setMerchantId(merchantId);
        payConfig.setAppId(appId);
        payConfig.setMerchantSerialNumber(merchantSerialNumber);
        payConfig.setPrivateKey(privateKeyString);
        //PemUtil.loadPrivateKeyFromPath(privateKeyPath)
        //设置平台证书
        List<X509Certificate> wechatPayCertificates = new ArrayList<>();
        wechatPayCertificates.add(PemUtil.loadX509FromPath(wechatPayCertificatePath));
        payConfig.setPayCertificate(wechatPayCertificateString);

        //创建支付订单
        Order order = new Order();
        order.setDescription("这是一个测试支付单");
        order.setNotifyUrl("http://www.tmfz999.com/api/common/wx/wx_pay");
        order.setAmount(new BigDecimal("0.01"));
        order.setOutTradeNo(IDGeneratorUtils.snowflake().toString());
        order.setOpenId(openId);

        //jsapi pay
//        JsapiApis apis = new JsapiApis(payConfig);
        H5Apis apis = new H5Apis(payConfig);
//        NativeApis apis = new NativeApis(payConfig);
        Map<String, String> result = null;

//        result = (new JsapiApis(payConfig)).pay(order);
//        System.out.println(result);
//        System.out.println("===========================================");
//        result = (new H5Apis(payConfig)).pay(order);
//        System.out.println(result);
//        System.out.println("===========================================");
//        result = (new NativeApis(payConfig)).pay(order);
//        System.out.println(result);
//        System.out.println("===========================================");

        PayOrderDetails payOrderDetails = (new NativeApis(payConfig)).orderDetailsByOutTradeNo("103097228935041024");
        System.out.println(payOrderDetails);
        return "hello";
    }

    /**
     * 支付宝支付测试
     *
     * @return
     * @throws AlipayApiException
     */
    @GetMapping("alipay")
    public String alipay() throws AlipayApiException {
        String privateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDZryZb8d+VRPtX3opQrYYpR3phnpUJqYm5aMMeFN7e/FJdHumL/mMa6Z419p3x2LnHzFX4VuZ9XlFtXkYx2SUSe0Wdh1qnFc7NULaiZTCYKLDmqFCq4HOdf9c3+4+Auu5MVEuN83Yb/tVXbuDlbpNsmwvtsa9sRwUH3XglKeaddeuELq1i0Mi0c0wACy+OKRwLczqkz4cmd8DdV1XWWMGpjWr5mNf/pmUR2UA3f/tISoMWqDFdGfh53D0aD02Ctjf8Qcd7IFDghuWxCqMqjlUefQsv70zuvO/LUoJyoKuwkViAx7Vnxf/yx+KJXUuEhEiJnM93jN5P73DSTUo3eu+bAgMBAAECggEBAMYOoiwNyYuak8TC6b2149cy/ZpMGvYLlw9mzgBUpOLlWFr5gSyqa6lU3Wnj1JjywzWSKLYm+tceNZ/4oo1AMIa8Of615DkZkmskAMeIocoPW8TBZ4PRa9/lLP/2lf5eFerE12l0FgYquhku/NhC30d1WkkN3bn6dA1P57yIfxbLQPJfnPzuq6Z0HiKIbSHItaENrk4QizLsQJa8+vurmKCU+skHM7lM0jJ8pY026n7iNhXSKWnhhJpgcm8+1SX9tFpml9T8ytwPCcGk25qhuRsPc0UwNFXpPlCqtFHZigqXLjjyUoEEn1MRd39JDRXYaGmhgj7xwe4KRJYZ2tUVOqECgYEA+zz+iuVjc0B/mrdmIm6PA4c89JFQiCcrlhrZ//afQ2jxSiebZqMM8Ywmbid8hCSb92ldUXn6taLph7KqbauoStgEnhrF2QLhDyD+eIHwfyd27jsOtzoSJ1Occ946b9rxky1CEWo1V7HveZUiaEC+6w9ZbzFT2js1Jpay8girIoUCgYEA3c9Z+KL8eCwVVQkTcQ/HDrzEIGqjNXSgdHQ5kBaMHtZwSbNuMsw9Wpb/MHj04vqeKITiDZFL872YlJrA4ukXU8Aoi1VyHcWY5iLDfAtbPU89avRHSnGml4QWr5weiU1MaK03Cp64uIAbmPMg652Y5OnULjZwymFNR+DmrNEQM58CgYEAsQhVxMHGxhuYzwiUa4bEN5RAG4WZ3ZbcW7UoD/lUWEfGXp5yHmyeXkaHfe9NCkBRtpEENLljNEJLlFcNSKJAwBxfhJkQ+M3SkLqLFZdaQ/8nCs+KmwnMaPG9uNfLDa6Vs0Oc3nCcjeLQADhaOmQFI9V5C6USlSBLX5mpFmuNS5ECgYEAwiiaMgnBhBPajHgIW6Sa8yyLZXDS8pznQ2jlZlpGR0wgOHNnF/IuFpksQiru+ay1OvSux3+TA4XIBPS9uSKeN8Jx+x1NagAoifKOOYvbTvyI35F6whBf6itGjGvVUwylwM826gXr3hzNi4HvJC/swRxpjhY7fvvTYQLeCRJr4zMCgYA9tg3ER33GK+sT+KgBVcUhmisLTV3lUPGSE7SxqXX1hhEJUkpb2Jn8mTKFM0ONfKq1m2WjLiOfbnER5YkpnOCJikIXdMTov5bCFwXCgwbhUOdChpApMG8fP2afLiAaRDIo4ty3mkrwQeK1pd5qf+ZXYIUvPMRBqx2LVIFzf1dwIw==";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhk7ucb773t6uAfUJd1MCyUyqPUuzF51sxGk6z4v0+rz7lo44zCnxgV+htJvvMRcqESOse9zxpXJ8MsGOMjDIcdIb0VC3/UFlwV5rHR8GLx87l7ryfEiPDNZg2PnTJUzXfe1NwueDV2ErGf5pjcstusQjhuFjjBVomqH7R8HxMPPDpdhgZC5r2i0rRGa/DnOE8E9y9tbuvOKJVvSrHdX+xLN25d5y+fT6g7r/cgTLBz8k17KUhu3MPy+Lx05JgXmyZiJRU/awHptlX/BthqauDLHJhGBKH4od4ZG0dGlkjHYBnODsNHdzU1b3SYsEScQgn9hAIB4PUmIdZJCRvnPTCwIDAQAB";
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setAppId("2021003157632368");
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF8");
        alipayConfig.setSignType("RSA2");
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo("70501111111S001111119");
        model.setTotalAmount("0.01");
        model.setSubject("大乐透");
        model.setProductCode("QUICK_WAP_WAY");
//        model.setSellerId("2088102147948060");
        request.setBizModel(model);
        AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);
        System.out.println(response.getBody());
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        return response.getBody();
    }

    @GetMapping("alipay2")
    public String alipay2() throws AlipayApiException {
        String privateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDZryZb8d+VRPtX3opQrYYpR3phnpUJqYm5aMMeFN7e/FJdHumL/mMa6Z419p3x2LnHzFX4VuZ9XlFtXkYx2SUSe0Wdh1qnFc7NULaiZTCYKLDmqFCq4HOdf9c3+4+Auu5MVEuN83Yb/tVXbuDlbpNsmwvtsa9sRwUH3XglKeaddeuELq1i0Mi0c0wACy+OKRwLczqkz4cmd8DdV1XWWMGpjWr5mNf/pmUR2UA3f/tISoMWqDFdGfh53D0aD02Ctjf8Qcd7IFDghuWxCqMqjlUefQsv70zuvO/LUoJyoKuwkViAx7Vnxf/yx+KJXUuEhEiJnM93jN5P73DSTUo3eu+bAgMBAAECggEBAMYOoiwNyYuak8TC6b2149cy/ZpMGvYLlw9mzgBUpOLlWFr5gSyqa6lU3Wnj1JjywzWSKLYm+tceNZ/4oo1AMIa8Of615DkZkmskAMeIocoPW8TBZ4PRa9/lLP/2lf5eFerE12l0FgYquhku/NhC30d1WkkN3bn6dA1P57yIfxbLQPJfnPzuq6Z0HiKIbSHItaENrk4QizLsQJa8+vurmKCU+skHM7lM0jJ8pY026n7iNhXSKWnhhJpgcm8+1SX9tFpml9T8ytwPCcGk25qhuRsPc0UwNFXpPlCqtFHZigqXLjjyUoEEn1MRd39JDRXYaGmhgj7xwe4KRJYZ2tUVOqECgYEA+zz+iuVjc0B/mrdmIm6PA4c89JFQiCcrlhrZ//afQ2jxSiebZqMM8Ywmbid8hCSb92ldUXn6taLph7KqbauoStgEnhrF2QLhDyD+eIHwfyd27jsOtzoSJ1Occ946b9rxky1CEWo1V7HveZUiaEC+6w9ZbzFT2js1Jpay8girIoUCgYEA3c9Z+KL8eCwVVQkTcQ/HDrzEIGqjNXSgdHQ5kBaMHtZwSbNuMsw9Wpb/MHj04vqeKITiDZFL872YlJrA4ukXU8Aoi1VyHcWY5iLDfAtbPU89avRHSnGml4QWr5weiU1MaK03Cp64uIAbmPMg652Y5OnULjZwymFNR+DmrNEQM58CgYEAsQhVxMHGxhuYzwiUa4bEN5RAG4WZ3ZbcW7UoD/lUWEfGXp5yHmyeXkaHfe9NCkBRtpEENLljNEJLlFcNSKJAwBxfhJkQ+M3SkLqLFZdaQ/8nCs+KmwnMaPG9uNfLDa6Vs0Oc3nCcjeLQADhaOmQFI9V5C6USlSBLX5mpFmuNS5ECgYEAwiiaMgnBhBPajHgIW6Sa8yyLZXDS8pznQ2jlZlpGR0wgOHNnF/IuFpksQiru+ay1OvSux3+TA4XIBPS9uSKeN8Jx+x1NagAoifKOOYvbTvyI35F6whBf6itGjGvVUwylwM826gXr3hzNi4HvJC/swRxpjhY7fvvTYQLeCRJr4zMCgYA9tg3ER33GK+sT+KgBVcUhmisLTV3lUPGSE7SxqXX1hhEJUkpb2Jn8mTKFM0ONfKq1m2WjLiOfbnER5YkpnOCJikIXdMTov5bCFwXCgwbhUOdChpApMG8fP2afLiAaRDIo4ty3mkrwQeK1pd5qf+ZXYIUvPMRBqx2LVIFzf1dwIw==";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhk7ucb773t6uAfUJd1MCyUyqPUuzF51sxGk6z4v0+rz7lo44zCnxgV+htJvvMRcqESOse9zxpXJ8MsGOMjDIcdIb0VC3/UFlwV5rHR8GLx87l7ryfEiPDNZg2PnTJUzXfe1NwueDV2ErGf5pjcstusQjhuFjjBVomqH7R8HxMPPDpdhgZC5r2i0rRGa/DnOE8E9y9tbuvOKJVvSrHdX+xLN25d5y+fT6g7r/cgTLBz8k17KUhu3MPy+Lx05JgXmyZiJRU/awHptlX/BthqauDLHJhGBKH4od4ZG0dGlkjHYBnODsNHdzU1b3SYsEScQgn9hAIB4PUmIdZJCRvnPTCwIDAQAB";

        PayConfig payConfig = new PayConfig();
        payConfig.setAppId("2021003157632368");
        payConfig.setPrivateKey(privateKey);
        payConfig.setPayCertificate(alipayPublicKey);

        Map<String, String> result = (new WebApis(payConfig)).pay(getOrder());
        return result.get("body");
    }

    /**
     * 获取支付订单
     * @return
     */
    private Order getOrder() {
        String openId = "oDo-_6mpN998CPd-X9dmVc0lLDas";
        //创建支付订单
        Order order = new Order();
        order.setDescription("这是一个测试支付单");
        order.setNotifyUrl("http://www.tmfz999.com/api/common/wx/wx_pay");
        order.setAmount(new BigDecimal("0.01"));
        order.setOutTradeNo(IDGeneratorUtils.snowflake().toString());
        order.setOpenId(openId);

        return order;
    }

    @GetMapping("t")
    public String t() {
        return "aaa";
    }

    @GetMapping("ta")
    public Object ta(HttpServletResponse response) throws IOException {
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto();
        orderDetailsDto.setOutTradeNo(IDGeneratorUtils.snowflake().toString());
        orderDetailsDto.setAmount(new BigDecimal("0.01"));
        orderDetailsDto.setDescription("支付测试单");
        orderDetailsDto.setNotifyUrl("http://www.baidu.com/");
        orderDetailsDto.setOutTradeNo(IDGeneratorUtils.snowflake().toString());
        orderDetailsDto.setPayInfoId(2);

        Map<String, String> result = payOrderService.pay(orderDetailsDto);
        System.out.println(result);
//        response.getOutputStream().write(result.get("body").getBytes(StandardCharsets.UTF_8));
//        return response;
//        return JSONUtil.toJsonStr(result);
        return result;
    }
}
