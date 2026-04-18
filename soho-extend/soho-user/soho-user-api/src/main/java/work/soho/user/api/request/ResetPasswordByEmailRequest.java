package work.soho.user.api.request;

import lombok.Data;

@Data
public class ResetPasswordByEmailRequest {
    /**
     * 邮箱地址。
     */
    private String email;

    /**
     * 邮箱验证码。
     */
    private String emailVerifyCode;

    /**
     * 新密码。
     */
    private String newPassword;

    /**
     * 确认新密码。
     */
    private String confirmPassword;
}
