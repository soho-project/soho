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

    /**
     * 支付展示标题
     */
    private String title;

    /**
     * 支付账户展示名称
     */
    private String accountName;

    /**
     * 收款二维码内容（复用 account_public_key 字段）
     */
    private String accountPublicKey;
}
