package work.soho.common.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import work.soho.common.redis.util.SerializerUtils;

/**
 * <p>
 * SohoRedisTemplate
 * </p>
 *
 * @author livk
 * @date 2022/1/21
 */
public class SohoRedisTemplate extends RedisTemplate<String, Object> {

    private SohoRedisTemplate() {
        this.setKeySerializer(RedisSerializer.string());
        this.setHashKeySerializer(RedisSerializer.string());
        this.setValueSerializer(SerializerUtils.getJacksonSerializer(Object.class));
        this.setHashValueSerializer(SerializerUtils.getJacksonSerializer(Object.class));
    }

    public SohoRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        this();
        this.setConnectionFactory(redisConnectionFactory);
        this.afterPropertiesSet();
    }
}
