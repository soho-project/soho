package work.soho.pay.biz.platform;

import lombok.Data;

@Data
public class PayConfig {
    /**
     * 支付平台ID
     * 本地配置信息ID
     */
    private Integer id;

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
