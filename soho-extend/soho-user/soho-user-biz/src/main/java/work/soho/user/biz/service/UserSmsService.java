package work.soho.user.biz.service;

public interface UserSmsService {
    void sendSmsCaptcha(Long userId);
    Boolean verifySmsCaptcha(Long userId, String code);

    void sendSmsCaptchaByPhone(String phone);
    Boolean verifySmsCaptchaByPhone(String phone, String code);
}
