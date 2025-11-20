package work.soho.user.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import work.soho.admin.api.service.SmsApiService;
import work.soho.user.api.service.UserSmsApiService;
import work.soho.user.biz.mapper.UserInfoMapper;
import work.soho.user.biz.service.UserSmsService;

import java.util.HashMap;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserSmsServiceImpl implements UserSmsService, UserSmsApiService {
    private final UserInfoMapper userInfoMapper;
    private final StringRedisTemplate redisTemplate;
    private final SmsApiService smsApiService;

    private static String USER_SEND_SMS_LASTTIME = "user_send_sms_lasttime";
    private static String USER_SEND_SMS_CODE = "user_send_sms_code";

    private static String PHONE_SEND_SMS_LASTTIME = "phone_send_sms_lasttime";
    private static String PHONE_SEND_SMS_CODE = "phone_send_sms_code";

    private String getUserSendSmsLastTimeKey(Long userId) {
        return USER_SEND_SMS_LASTTIME + userId;
    }

    private String getUserSendSmsCodeKey(Long userId) {
        return USER_SEND_SMS_CODE + userId;
    }

    private void setUserSendSmsLasttime(Long userId) {
        String key = getUserSendSmsLastTimeKey(userId);
        redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()));
    }

    private Long getUserSendSmsLasttime(Long userId) {
        String key = getUserSendSmsLastTimeKey(userId);
        String value = redisTemplate.opsForValue().get(key);
        return value == null ? null : Long.parseLong(value);
    }

    private void setUserSendSmsCode(Long userId, String code) {
        String key = getUserSendSmsCodeKey(userId);
        redisTemplate.opsForValue().set(key, code);
    }

    private String getUserSendSmsCode(Long userId) {
        String key = getUserSendSmsCodeKey(userId);
        return redisTemplate.opsForValue().get(key);
    }

    private String randomCode() {
        Random random = new Random();
        int sixDigit = 100000 + random.nextInt(900000);
        String code = String.valueOf(sixDigit);
        return code;
    }

    @Override
    public void sendSmsCaptcha(Long userId) {
        Long lasttime = getUserSendSmsLasttime(userId);
        if(lasttime != null && System.currentTimeMillis() - lasttime < 60 * 1000) {
            throw new RuntimeException("请勿频繁发送验证码");
        }

        String code = randomCode();
        setUserSendSmsCode(userId, code);

        // 获取用户手机号
        String phone = userInfoMapper.selectById(userId).getPhone();
        HashMap<String, String> map  = new HashMap<>();
        map.put("code", code);
        smsApiService.sendSms(phone, "code", map);
        setUserSendSmsLasttime(userId);
    }

    @Override
    public Boolean verifySmsCaptcha(Long userId, String code) {
        String getUserSendSmsCode = getUserSendSmsCode(userId);
        if(getUserSendSmsCode == null) {
            return false;
        }
        if(!code.equals(getUserSendSmsCode)) {
            return false;
        }
        redisTemplate.delete(getUserSendSmsCodeKey(userId));
        redisTemplate.delete(getUserSendSmsLastTimeKey(userId));
        return true;
    }

    private String getPhoneSendSmsLastTimeKey(String phone) {
        return PHONE_SEND_SMS_LASTTIME + phone;
    }

    private Long getPhoneSendSmsLasttime(String phone) {
        String key = getPhoneSendSmsLastTimeKey(phone);
        String value = redisTemplate.opsForValue().get(key);
        return value == null ? null : Long.parseLong(value);
    }

    private String getPhoneSendSmsCodeKey(String phone) {
        return PHONE_SEND_SMS_CODE + phone;
    }

    private String getPhoneSendSmsCode(String phone) {
        String key = getPhoneSendSmsCodeKey(phone);
        return redisTemplate.opsForValue().get(key);
    }

    private void setPhoneSendSmsLasttime(String phone) {
        String key = getPhoneSendSmsLastTimeKey(phone);
        redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()));
    }

    private void setPhoneSendSmsCode(String phone, String code) {
        String key = getPhoneSendSmsCodeKey(phone);
        redisTemplate.opsForValue().set(key, code);
    }

    @Override
    public void sendSmsCaptchaByPhone(String phone) {
        Long lasttime = getPhoneSendSmsLasttime(phone);
        if(lasttime != null && System.currentTimeMillis() - lasttime < 60 * 1000) {
            throw new RuntimeException("请勿频繁发送验证码");
        }

        String code = randomCode();
        setPhoneSendSmsCode(phone, code);

        // 获取用户手机号
        HashMap<String, String> map  = new HashMap<>();
        map.put("code", code);
        smsApiService.sendSms(phone, "code", map);
        setPhoneSendSmsLasttime( phone);
    }

    @Override
    public Boolean verifySmsCaptchaByPhone(String phone, String code) {
        String oldCode = getPhoneSendSmsCode(phone);
        if(oldCode == null) {
            return false;
        }
        if(!code.equals(oldCode)) {
            return false;
        }
        redisTemplate.delete(getPhoneSendSmsCodeKey(phone));
        redisTemplate.delete(getPhoneSendSmsLastTimeKey(phone));
        return true;
    }
}
