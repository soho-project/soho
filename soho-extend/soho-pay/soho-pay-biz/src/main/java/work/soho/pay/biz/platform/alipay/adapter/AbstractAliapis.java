package work.soho.pay.biz.platform.alipay.adapter;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import work.soho.pay.biz.platform.PayConfig;

/**
 * 阿里接口基类
 */
abstract public class AbstractAliapis {
    private PayConfig payConfig;

    public AbstractAliapis(PayConfig payConfig) {
        this.payConfig = payConfig;
    }

    /**
     * 获取阿里接口请求api
     * 
     * @return
     */
    public AlipayClient getAliClient() throws AlipayApiException {
        //String privateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDZryZb8d+VRPtX3opQrYYpR3phnpUJqYm5aMMeFN7e/FJdHumL/mMa6Z419p3x2LnHzFX4VuZ9XlFtXkYx2SUSe0Wdh1qnFc7NULaiZTCYKLDmqFCq4HOdf9c3+4+Auu5MVEuN83Yb/tVXbuDlbpNsmwvtsa9sRwUH3XglKeaddeuELq1i0Mi0c0wACy+OKRwLczqkz4cmd8DdV1XWWMGpjWr5mNf/pmUR2UA3f/tISoMWqDFdGfh53D0aD02Ctjf8Qcd7IFDghuWxCqMqjlUefQsv70zuvO/LUoJyoKuwkViAx7Vnxf/yx+KJXUuEhEiJnM93jN5P73DSTUo3eu+bAgMBAAECggEBAMYOoiwNyYuak8TC6b2149cy/ZpMGvYLlw9mzgBUpOLlWFr5gSyqa6lU3Wnj1JjywzWSKLYm+tceNZ/4oo1AMIa8Of615DkZkmskAMeIocoPW8TBZ4PRa9/lLP/2lf5eFerE12l0FgYquhku/NhC30d1WkkN3bn6dA1P57yIfxbLQPJfnPzuq6Z0HiKIbSHItaENrk4QizLsQJa8+vurmKCU+skHM7lM0jJ8pY026n7iNhXSKWnhhJpgcm8+1SX9tFpml9T8ytwPCcGk25qhuRsPc0UwNFXpPlCqtFHZigqXLjjyUoEEn1MRd39JDRXYaGmhgj7xwe4KRJYZ2tUVOqECgYEA+zz+iuVjc0B/mrdmIm6PA4c89JFQiCcrlhrZ//afQ2jxSiebZqMM8Ywmbid8hCSb92ldUXn6taLph7KqbauoStgEnhrF2QLhDyD+eIHwfyd27jsOtzoSJ1Occ946b9rxky1CEWo1V7HveZUiaEC+6w9ZbzFT2js1Jpay8girIoUCgYEA3c9Z+KL8eCwVVQkTcQ/HDrzEIGqjNXSgdHQ5kBaMHtZwSbNuMsw9Wpb/MHj04vqeKITiDZFL872YlJrA4ukXU8Aoi1VyHcWY5iLDfAtbPU89avRHSnGml4QWr5weiU1MaK03Cp64uIAbmPMg652Y5OnULjZwymFNR+DmrNEQM58CgYEAsQhVxMHGxhuYzwiUa4bEN5RAG4WZ3ZbcW7UoD/lUWEfGXp5yHmyeXkaHfe9NCkBRtpEENLljNEJLlFcNSKJAwBxfhJkQ+M3SkLqLFZdaQ/8nCs+KmwnMaPG9uNfLDa6Vs0Oc3nCcjeLQADhaOmQFI9V5C6USlSBLX5mpFmuNS5ECgYEAwiiaMgnBhBPajHgIW6Sa8yyLZXDS8pznQ2jlZlpGR0wgOHNnF/IuFpksQiru+ay1OvSux3+TA4XIBPS9uSKeN8Jx+x1NagAoifKOOYvbTvyI35F6whBf6itGjGvVUwylwM826gXr3hzNi4HvJC/swRxpjhY7fvvTYQLeCRJr4zMCgYA9tg3ER33GK+sT+KgBVcUhmisLTV3lUPGSE7SxqXX1hhEJUkpb2Jn8mTKFM0ONfKq1m2WjLiOfbnER5YkpnOCJikIXdMTov5bCFwXCgwbhUOdChpApMG8fP2afLiAaRDIo4ty3mkrwQeK1pd5qf+ZXYIUvPMRBqx2LVIFzf1dwIw==";
        //String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhk7ucb773t6uAfUJd1MCyUyqPUuzF51sxGk6z4v0+rz7lo44zCnxgV+htJvvMRcqESOse9zxpXJ8MsGOMjDIcdIb0VC3/UFlwV5rHR8GLx87l7ryfEiPDNZg2PnTJUzXfe1NwueDV2ErGf5pjcstusQjhuFjjBVomqH7R8HxMPPDpdhgZC5r2i0rRGa/DnOE8E9y9tbuvOKJVvSrHdX+xLN25d5y+fT6g7r/cgTLBz8k17KUhu3MPy+Lx05JgXmyZiJRU/awHptlX/BthqauDLHJhGBKH4od4ZG0dGlkjHYBnODsNHdzU1b3SYsEScQgn9hAIB4PUmIdZJCRvnPTCwIDAQAB";
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setAppId(payConfig.getAppId());
        alipayConfig.setPrivateKey(payConfig.getPrivateKey());
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(payConfig.getPayCertificate());
        alipayConfig.setCharset("UTF8");
        alipayConfig.setSignType("RSA2");
        return new DefaultAlipayClient(alipayConfig);
    }
}
