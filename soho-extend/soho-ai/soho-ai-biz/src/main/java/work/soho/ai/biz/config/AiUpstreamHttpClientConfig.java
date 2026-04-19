package work.soho.ai.biz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

/**
 * AI 上游 HTTP 连接池配置。
 */
@Configuration
public class AiUpstreamHttpClientConfig {
    private static final String CONNECTION_PROVIDER_NAME = "ai-upstream-http";
    private static final int MAX_CONNECTIONS = 200;
    private static final int PENDING_ACQUIRE_MAX_COUNT = 1000;
    private static final Duration PENDING_ACQUIRE_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration MAX_IDLE_TIME = Duration.ofSeconds(30);
    private static final Duration MAX_LIFE_TIME = Duration.ofMinutes(10);
    private static final Duration EVICT_IN_BACKGROUND = Duration.ofSeconds(30);

    /**
     * 共享 Reactor Netty 连接池。
     */
    @Bean(destroyMethod = "dispose")
    public ConnectionProvider aiUpstreamConnectionProvider() {
        return ConnectionProvider.builder(CONNECTION_PROVIDER_NAME)
                .maxConnections(MAX_CONNECTIONS)
                .pendingAcquireMaxCount(PENDING_ACQUIRE_MAX_COUNT)
                .pendingAcquireTimeout(PENDING_ACQUIRE_TIMEOUT)
                .maxIdleTime(MAX_IDLE_TIME)
                .maxLifeTime(MAX_LIFE_TIME)
                .evictInBackground(EVICT_IN_BACKGROUND)
                .build();
    }
}
