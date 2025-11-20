package work.soho.wallet.api.request;

import lombok.Data;

/**
 * 更新支付密码
 */
@Data
public class UpdatePayPasswordRequest {
    /**
     * 旧密码
     *
     * 验证码 二选一
     */
    private String oldPayPassword;
    private String newPayPassword;
    private String confirmPayPassword;
    /**
     * 验证码
     *
     * 旧密码 二选一
     */
    private String captcha;
}
