package work.soho.pay.biz.platform;

import lombok.Data;

@Data
public class PayConfig {
    /**
     * 应用ID
     */
    private String appId;

    /**
     * 商户号ID
     */
    private String merchantId;

    /**
     * 本地私钥序列号
     */
    private String merchantSerialNumber;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 平台证书
     */
    private String payCertificate;
}
