package work.soho.user.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import work.soho.admin.api.service.EmailApiService;
import work.soho.common.core.util.StringUtils;
import work.soho.user.biz.service.UserEmailCodeService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserEmailCodeServiceImpl implements UserEmailCodeService {
    private static final String REGISTER_EMAIL_SCENE = "register";
    private static final String RESET_PASSWORD_EMAIL_SCENE = "reset_password";
    private static final long SEND_INTERVAL_MILLIS = 60_000L;
    private static final long CODE_EXPIRE_MINUTES = 10L;

    private final StringRedisTemplate redisTemplate;
    private final EmailApiService emailApiService;

    /**
     * 发送注册邮箱验证码。
     *
     * @param email 邮箱地址
     */
    @Override
    public void sendRegisterEmailCode(String email) {
        sendEmailCode(email, REGISTER_EMAIL_SCENE);
    }

    /**
     * 发送找回密码邮箱验证码。
     *
     * @param email 邮箱地址
     */
    @Override
    public void sendResetPasswordEmailCode(String email) {
        sendEmailCode(email, RESET_PASSWORD_EMAIL_SCENE);
    }

    /**
     * 校验注册邮箱验证码，校验成功后会删除验证码。
     *
     * @param email 邮箱地址
     * @param code 验证码
     * @return 是否校验成功
     */
    @Override
    public Boolean verifyRegisterEmailCode(String email, String code) {
        return verifyEmailCode(email, code, REGISTER_EMAIL_SCENE);
    }

    /**
     * 校验找回密码邮箱验证码，校验成功后会删除验证码。
     *
     * @param email 邮箱地址
     * @param code 验证码
     * @return 是否校验成功
     */
    @Override
    public Boolean verifyResetPasswordEmailCode(String email, String code) {
        return verifyEmailCode(email, code, RESET_PASSWORD_EMAIL_SCENE);
    }

    /**
     * 发送指定场景的邮箱验证码。
     *
     * @param email 邮箱地址
     * @param scene 业务场景
     */
    private void sendEmailCode(String email, String scene) {
        Long lastTime = getSendLastTime(email, scene);
        if (lastTime != null && System.currentTimeMillis() - lastTime < SEND_INTERVAL_MILLIS) {
            throw new RuntimeException("请勿频繁发送验证码");
        }

        String code = randomCode();
        Map<String, Object> model = new HashMap<>(1);
        model.put("code", code);
        emailApiService.sendEmail(email, "code", model);

        redisTemplate.opsForValue().set(buildCodeKey(email, scene), code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        setSendLastTime(email, scene, System.currentTimeMillis());
    }

    /**
     * 校验指定场景的邮箱验证码。
     *
     * @param email 邮箱地址
     * @param code 验证码
     * @param scene 业务场景
     * @return 是否校验成功
     */
    private Boolean verifyEmailCode(String email, String code, String scene) {
        if (StringUtils.isBlank(email) || StringUtils.isBlank(code)) {
            return false;
        }
        String oldCode = redisTemplate.opsForValue().get(buildCodeKey(email, scene));
        if (StringUtils.isBlank(oldCode) || !code.equals(oldCode)) {
            return false;
        }
        redisTemplate.delete(buildCodeKey(email, scene));
        redisTemplate.delete(buildSendLastTimeKey(email, scene));
        return true;
    }

    /**
     * 获取邮箱发送验证码限流时间戳。
     *
     * @param email 邮箱
     * @param scene 业务场景
     * @return 时间戳
     */
    private Long getSendLastTime(String email, String scene) {
        String value = redisTemplate.opsForValue().get(buildSendLastTimeKey(email, scene));
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Long.parseLong(value);
    }

    /**
     * 设置邮箱发送验证码限流时间戳。
     *
     * @param email 邮箱
     * @param scene 业务场景
     * @param value 时间戳
     */
    private void setSendLastTime(String email, String scene, long value) {
        redisTemplate.opsForValue().set(buildSendLastTimeKey(email, scene), String.valueOf(value), 5, TimeUnit.MINUTES);
    }

    /**
     * 构建邮箱验证码存储键。
     *
     * @param email 邮箱
     * @param scene 业务场景
     * @return redis key
     */
    private String buildCodeKey(String email, String scene) {
        return "user_email_code:" + scene + ":" + email;
    }

    /**
     * 构建邮箱验证码发送时间存储键。
     *
     * @param email 邮箱
     * @param scene 业务场景
     * @return redis key
     */
    private String buildSendLastTimeKey(String email, String scene) {
        return "user_email_send_lasttime:" + scene + ":" + email;
    }

    /**
     * 生成6位数字验证码。
     *
     * @return 验证码
     */
    private String randomCode() {
        Random random = new Random();
        int sixDigit = 100000 + random.nextInt(900000);
        return String.valueOf(sixDigit);
    }
}
