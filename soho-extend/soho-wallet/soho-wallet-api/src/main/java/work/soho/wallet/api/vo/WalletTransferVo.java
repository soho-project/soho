package work.soho.wallet.api.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletTransferVo {
    /**
     * 转出钱包ID
     */
    private Long fromWalletId;

    /**
     * 转入钱包ID
     *
     * toPhone 二选一
     */
    private Long toWalletId;

    /**
     * 转入手机号
     *
     * 优先使用； toWalletId 二选一
     */
    private String toPhone;

    /**
     * 转入钱包类型
     */
    private Integer toWalletType;

    /**
     * 转账金额
     */
    private BigDecimal amount;

    /**
     * 支付密码
     */
    private String payPassword;

    /**
     * 转账备注
     */
    private String remark;
}
