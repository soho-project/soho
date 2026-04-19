package work.soho.ai.biz.service.impl;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import reactor.netty.resources.ConnectionProvider;
import work.soho.ai.biz.utils.AiProxyLayerUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 上游客户端工厂缓存测试。
 */
public class AiUpstreamClientFactoryImplTest {

    /**
     * 记录客户端获取次数的工厂子类。
     */
    private static final class CountingAiUpstreamClientFactoryImpl extends AiUpstreamClientFactoryImpl {
        private int getWebClientCalls;

        private CountingAiUpstreamClientFactoryImpl(ConnectionProvider provider) {
            super(provider);
        }

        /**
         * 统计流式调用命中的客户端获取次数。
         */
        @Override
        public org.springframework.web.reactive.function.client.WebClient getWebClient(Integer timeoutMs, AiProxyLayerUtils.ProxySettings proxySettings) {
            getWebClientCalls++;
            return super.getWebClient(timeoutMs, proxySettings);
        }
    }

    @Test
    public void getWebClient_whenSameTimeoutAndProxy_shouldReuseInstance() {
        ConnectionProvider provider = ConnectionProvider.create("test-ai-upstream");
        try {
            AiUpstreamClientFactoryImpl factory = new AiUpstreamClientFactoryImpl(provider);
            AiProxyLayerUtils.ProxySettings settings = AiProxyLayerUtils.resolve(java.util.Map.of(
                    "proxyType", "http",
                    "proxyHost", "127.0.0.1",
                    "proxyPort", 7890
            ));

            Object first = factory.getWebClient(3000, settings);
            Object second = factory.getWebClient(3000, settings);

            assertThat(first).isSameAs(second);
        } finally {
            provider.dispose();
        }
    }

    @Test
    public void getWebClient_whenTimeoutDiffers_shouldCreateDifferentInstances() {
        ConnectionProvider provider = ConnectionProvider.create("test-ai-upstream-2");
        try {
            AiUpstreamClientFactoryImpl factory = new AiUpstreamClientFactoryImpl(provider);
            Object first = factory.getWebClient(3000, null);
            Object second = factory.getWebClient(5000, null);

            assertThat(first).isNotSameAs(second);
        } finally {
            provider.dispose();
        }
    }

    @Test
    public void getWebClient_whenProxyDiffers_shouldCreateDifferentInstances() {
        ConnectionProvider provider = ConnectionProvider.create("test-ai-upstream-3");
        try {
            AiUpstreamClientFactoryImpl factory = new AiUpstreamClientFactoryImpl(provider);
            AiProxyLayerUtils.ProxySettings firstProxy = AiProxyLayerUtils.resolve(java.util.Map.of(
                    "proxyType", "http",
                    "proxyHost", "127.0.0.1",
                    "proxyPort", 7890
            ));
            AiProxyLayerUtils.ProxySettings secondProxy = AiProxyLayerUtils.resolve(java.util.Map.of(
                    "proxyType", "http",
                    "proxyHost", "127.0.0.1",
                    "proxyPort", 7891
            ));

            Object first = factory.getWebClient(3000, firstProxy);
            Object second = factory.getWebClient(3000, secondProxy);

            assertThat(first).isNotSameAs(second);
        } finally {
            provider.dispose();
        }
    }

    @Test
    public void exchangeStream_whenCalled_shouldReuseCachedWebClient() {
        ConnectionProvider provider = ConnectionProvider.create("test-ai-upstream-4");
        try {
            CountingAiUpstreamClientFactoryImpl factory = new CountingAiUpstreamClientFactoryImpl(provider);
            AiProxyLayerUtils.ProxySettings proxy = AiProxyLayerUtils.resolve(java.util.Map.of(
                    "proxyType", "http",
                    "proxyHost", "127.0.0.1",
                    "proxyPort", 7890
            ));

            Object cached = factory.getWebClient(3000, proxy);

            try {
                factory.exchangeStream("http://127.0.0.1:9/stream", HttpMethod.POST, new HttpHeaders(), java.util.Map.of("x", 1), 3000, proxy)
                        .collectList()
                        .block();
            } catch (Exception ignore) {
            }

            Object reused = factory.getWebClient(3000, proxy);

            assertThat(reused).isSameAs(cached);
            assertThat(factory.getWebClientCalls).isGreaterThanOrEqualTo(3);
        } finally {
            provider.dispose();
        }
    }
}
