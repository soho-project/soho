package work.soho.user.api.service;

public interface UserSmsApiService {
    Boolean verifySmsCaptcha(Long userId, String code);
}
