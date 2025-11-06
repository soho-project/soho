package work.soho.pay.api.vo;

import lombok.Data;

@Data
public class HftPayCreateWallet {
    /**
     * 创建跳转的URL地址
     */
    private String jump;

    /**
     * 用户钱包ID
     */
    private String userCustId;
}
