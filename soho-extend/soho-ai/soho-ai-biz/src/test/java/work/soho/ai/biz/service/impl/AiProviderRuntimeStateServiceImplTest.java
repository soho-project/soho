package work.soho.ai.biz.service.impl;

import org.junit.Test;
import work.soho.ai.biz.domain.AiProviderConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class AiProviderRuntimeStateServiceImplTest {

    @Test
    public void recordFailure_whenTimeoutLike_shouldOpenCircuitImmediately() {
        AiProviderRuntimeStateServiceImpl service = new AiProviderRuntimeStateServiceImpl();
        AiProviderConfig providerConfig = buildProviderConfig(1L, 10);

        service.recordFailure(providerConfig, new IllegalStateException("upstream first token timeout"));

        assertThat(service.isRequestAllowed(providerConfig)).isFalse();
        assertThat(service.getEffectiveWeight(providerConfig)).isZero();
    }

    @Test
    public void recordSuccess_whenSlowTwice_shouldReduceAvailability() {
        AiProviderRuntimeStateServiceImpl service = new AiProviderRuntimeStateServiceImpl();
        AiProviderConfig providerConfig = buildProviderConfig(2L, 10);

        service.recordSuccess(providerConfig, 16_000L, 8_500L);
        assertThat(service.isRequestAllowed(providerConfig)).isTrue();

        service.recordSuccess(providerConfig, 16_500L, 8_800L);

        assertThat(service.isRequestAllowed(providerConfig)).isFalse();
        assertThat(service.getEffectiveWeight(providerConfig)).isZero();
    }

    @Test
    public void getEffectiveWeight_whenLatencyIncreases_shouldLowerWeightButKeepPositive() {
        AiProviderRuntimeStateServiceImpl service = new AiProviderRuntimeStateServiceImpl();
        AiProviderConfig providerConfig = buildProviderConfig(3L, 10);

        service.recordSuccess(providerConfig, 20_000L, 10_000L);

        assertThat(service.isRequestAllowed(providerConfig)).isTrue();
        assertThat(service.getEffectiveWeight(providerConfig)).isBetween(1, 9);
    }

    /**
     * 构造测试用提供方配置。
     *
     * @param id 主键
     * @param weight 基础权重
     * @return 提供方配置
     */
    private AiProviderConfig buildProviderConfig(Long id, Integer weight) {
        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setId(id);
        providerConfig.setWeight(weight);
        providerConfig.setCode("provider-" + id);
        return providerConfig;
    }
}
