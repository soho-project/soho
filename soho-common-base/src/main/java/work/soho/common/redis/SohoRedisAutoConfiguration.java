package work.soho.common.redis;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * <p>
 * SohoRedisAutoConfiguration
 * </p>
 *
 * @author livk
 * @date 2022/1/22
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class SohoRedisAutoConfiguration {

    @Bean
    public SohoRedisTemplate sohoRedisTemplate(LettuceConnectionFactory connectionFactory) {
        return new SohoRedisTemplate(connectionFactory);
    }
}
