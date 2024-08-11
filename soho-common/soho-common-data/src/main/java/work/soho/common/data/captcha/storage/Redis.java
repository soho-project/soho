package work.soho.common.data.captcha.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * redis 方式存储验证码
 */
@Component
@RequiredArgsConstructor
public class Redis implements StorageInterface {
    private final RedisTemplate redisTemplate;

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void drop(String key) {
        redisTemplate.delete(key);
    }
}
