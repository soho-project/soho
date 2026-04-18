package work.soho.ai.biz.service.impl;

import org.junit.Test;
import work.soho.ai.biz.domain.AiProxyConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class AiProxyRuntimeStateServiceImplTest {

    @Test
    public void recordFailure_whenProxyTimeoutLike_shouldOpenCircuitImmediately() {
        AiProxyRuntimeStateServiceImpl service = new AiProxyRuntimeStateServiceImpl();
        AiProxyConfig proxyConfig = buildProxyConfig(1L, 10);

        service.recordFailure(proxyConfig.getId(), new IllegalStateException("proxy connect timeout"));

        assertThat(service.isRequestAllowed(proxyConfig)).isFalse();
        assertThat(service.getEffectiveWeight(proxyConfig)).isZero();
    }

    @Test
    public void recordFailure_whenUpstreamBusinessError_shouldIgnoreProxyPenalty() {
        AiProxyRuntimeStateServiceImpl service = new AiProxyRuntimeStateServiceImpl();
        AiProxyConfig proxyConfig = buildProxyConfig(2L, 8);

        service.recordFailure(proxyConfig.getId(), new IllegalStateException("upstream response status 429"));

        assertThat(service.isRequestAllowed(proxyConfig)).isTrue();
        assertThat(service.getEffectiveWeight(proxyConfig)).isEqualTo(8);
    }

    @Test
    public void recordSuccess_whenLatencyIncreases_shouldLowerWeightButKeepPositive() {
        AiProxyRuntimeStateServiceImpl service = new AiProxyRuntimeStateServiceImpl();
        AiProxyConfig proxyConfig = buildProxyConfig(3L, 10);

        service.recordSuccess(proxyConfig.getId(), 20_000L);

        assertThat(service.isRequestAllowed(proxyConfig)).isTrue();
        assertThat(service.getEffectiveWeight(proxyConfig)).isBetween(1, 9);
    }

    @Test
    public void clearState_whenProxyHasRuntimePenalty_shouldRestoreBaseWeight() {
        AiProxyRuntimeStateServiceImpl service = new AiProxyRuntimeStateServiceImpl();
        AiProxyConfig proxyConfig = buildProxyConfig(4L, 6);

        service.recordFailure(proxyConfig.getId(), new IllegalStateException("proxy dns timeout"));
        assertThat(service.isRequestAllowed(proxyConfig)).isFalse();

        service.clearState(proxyConfig.getId());

        assertThat(service.isRequestAllowed(proxyConfig)).isTrue();
        assertThat(service.getEffectiveWeight(proxyConfig)).isEqualTo(6);
    }

    /**
     * 构造测试用代理配置。
     *
     * @param id 主键
     * @param weight 基础权重
     * @return 代理配置
     */
    private AiProxyConfig buildProxyConfig(Long id, Integer weight) {
        AiProxyConfig proxyConfig = new AiProxyConfig();
        proxyConfig.setId(id);
        proxyConfig.setWeight(weight);
        proxyConfig.setName("proxy-" + id);
        return proxyConfig;
    }
}
