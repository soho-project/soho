package work.soho.common.data.lock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95
 */
@Configuration
public class RedissonConfig {
    @Value("${redisson.nodeAddress}")
    private String redisHost;

    @Bean
    @Lazy
    public RedissonClient getRedissonClient() {
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.useSingleServer().setAddress(redisHost).setDatabase(0);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
