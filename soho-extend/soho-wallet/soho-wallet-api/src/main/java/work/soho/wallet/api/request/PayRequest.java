package work.soho.wallet.api.request;

import lombok.Data;

@Data
public class PayRequest {
    /**
     * 支付密码
     */
    private String payPassword;

    /**
     * 重置码
     */
    private String payCode;

    /**
     * 钱包类型名称
     */
    private String walletTypeName;
}
