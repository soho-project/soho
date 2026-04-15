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
    private static final String REGISTER_EMAIL_SEND_LAST_TIME_KEY = "register_email_send_lasttime:";
    private static final String REGISTER_EMAIL_CODE_KEY = "register_email_code:";
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
        Long lastTime = getSendLastTime(email);
        if (lastTime != null && System.currentTimeMillis() - lastTime < SEND_INTERVAL_MILLIS) {
            throw new RuntimeException("请勿频繁发送验证码");
        }

        String code = randomCode();
        Map<String, Object> model = new HashMap<>(1);
        model.put("code", code);
        emailApiService.sendEmail(email, "code", model);

        redisTemplate.opsForValue().set(buildCodeKey(email), code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        setSendLastTime(email, System.currentTimeMillis());
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
        if (StringUtils.isBlank(email) || StringUtils.isBlank(code)) {
            return false;
        }
        String oldCode = redisTemplate.opsForValue().get(buildCodeKey(email));
        if (StringUtils.isBlank(oldCode) || !code.equals(oldCode)) {
            return false;
        }
        redisTemplate.delete(buildCodeKey(email));
        redisTemplate.delete(buildSendLastTimeKey(email));
        return true;
    }

    /**
     * 获取邮箱发送验证码限流时间戳。
     *
     * @param email 邮箱
     * @return 时间戳
     */
    private Long getSendLastTime(String email) {
        String value = redisTemplate.opsForValue().get(buildSendLastTimeKey(email));
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Long.parseLong(value);
    }

    /**
     * 设置邮箱发送验证码限流时间戳。
     *
     * @param email 邮箱
     * @param value 时间戳
     */
    private void setSendLastTime(String email, long value) {
        redisTemplate.opsForValue().set(buildSendLastTimeKey(email), String.valueOf(value), 5, TimeUnit.MINUTES);
    }

    /**
     * 构建邮箱验证码存储键。
     *
     * @param email 邮箱
     * @return redis key
     */
    private String buildCodeKey(String email) {
        return REGISTER_EMAIL_CODE_KEY + email;
    }

    /**
     * 构建邮箱验证码发送时间存储键。
     *
     * @param email 邮箱
     * @return redis key
     */
    private String buildSendLastTimeKey(String email) {
        return REGISTER_EMAIL_SEND_LAST_TIME_KEY + email;
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
