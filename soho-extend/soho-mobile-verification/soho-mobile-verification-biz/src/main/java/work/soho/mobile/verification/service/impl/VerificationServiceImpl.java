package work.soho.mobile.verification.service.impl;

import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.mobile.verification.api.service.VerificationServiceApi;
import work.soho.mobile.verification.api.vo.VerificationCodeVo;
import work.soho.mobile.verification.service.VerificationService;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class VerificationServiceImpl implements VerificationService, VerificationServiceApi {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private static String PUSH_SMS_KEY_PREFIX = "push-sms-key-prefix-";

    private String getKeyById(String id) {
        return PUSH_SMS_KEY_PREFIX + id;
    }

    /**
     * 根据ID获取认证信息
     *
     * @param id
     * @return
     */
    public VerificationCodeVo getById(String id) {
        String body =  (String) redisTemplate.opsForValue().get(getKeyById(id));
        System.out.printf(body);
        if(body == null) {
            return null;
        }
        return JacksonUtils.toBean(body, VerificationCodeVo.class);
    }

    /**
     * 推送短信接口
     *
     * @param phoneNumber
     * @param msg
     */
    @Override
    public void pushMsg(String phoneNumber, String msg) {
        VerificationCodeVo verificationCodeVo = getById(msg);
        Assert.notNull(verificationCodeVo);
        verificationCodeVo.setIsSuccess(true);
        if(verificationCodeVo.getPhone() == null) {
            verificationCodeVo.setPhone(phoneNumber);
        } else {
            Assert.equals(verificationCodeVo.getPhone(), phoneNumber, "手机号错误！");
        }
        redisTemplate.opsForValue().set(getKeyById(msg), JacksonUtils.toJson(verificationCodeVo));
    }

    /**
     * 创建认证
     *
     * @return
     */
    @Override
    public String createVerification() {
        return createVerification(null);
    }

    /**
     * 创建认证
     *
     * @param phone
     * @return
     */
    @Override
    public String createVerification(String phone) {
        VerificationCodeVo verificationCodeVo = new VerificationCodeVo();
        String id = IDGeneratorUtils.snowflake().toString();
        verificationCodeVo.setId(id);
        verificationCodeVo.setIsSuccess(false);
        verificationCodeVo.setPhone(phone);
        verificationCodeVo.setCreateTime(LocalDateTime.now());
        redisTemplate.opsForValue().set(getKeyById(id), JacksonUtils.toJson(verificationCodeVo));
        redisTemplate.expire(id, Duration.ofSeconds(300));
        return id;
    }

}
