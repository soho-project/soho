package work.soho.user.api.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    /**
     * 旧密码
     *
     * 与验证码 二选一
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认密码
     */
    private String confirmPassword;

    /**
     * 验证码
     *
     * 与旧密码 二选一
     */
    private String captcha;
}
