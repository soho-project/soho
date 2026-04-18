package work.soho.user.biz.service;

public interface UserEmailCodeService {
    /**
     * 发送注册邮箱验证码。
     *
     * @param email 邮箱地址
     */
    void sendRegisterEmailCode(String email);

    /**
     * 发送找回密码邮箱验证码。
     *
     * @param email 邮箱地址
     */
    void sendResetPasswordEmailCode(String email);

    /**
     * 校验注册邮箱验证码，校验成功后会删除验证码。
     *
     * @param email 邮箱地址
     * @param code 验证码
     * @return 是否校验成功
     */
    Boolean verifyRegisterEmailCode(String email, String code);

    /**
     * 校验找回密码邮箱验证码，校验成功后会删除验证码。
     *
     * @param email 邮箱地址
     * @param code 验证码
     * @return 是否校验成功
     */
    Boolean verifyResetPasswordEmailCode(String email, String code);
}
