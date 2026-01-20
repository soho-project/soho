package work.soho.longlink.cloud.biz.broadcast;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import work.soho.longlink.cloud.api.message.LongLinkBroadcastChannels;

@Configuration
@RequiredArgsConstructor
public class LongLinkRedisBroadcastConfig {
    private final RedisConnectionFactory redisConnectionFactory;
    private final LongLinkRedisBroadcastListener listener;

    @Bean
    public RedisMessageListenerContainer longLinkRedisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(listener, new ChannelTopic(LongLinkBroadcastChannels.SENDER_CHANNEL));
        return container;
    }
}
