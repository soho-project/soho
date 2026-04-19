package work.soho.ai.biz.service.impl;

import org.junit.Test;
import reactor.netty.resources.ConnectionProvider;
import work.soho.ai.biz.utils.AiProxyLayerUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 上游客户端工厂缓存测试。
 */
public class AiUpstreamClientFactoryImplTest {

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
}
