package work.soho.ai.biz.service.impl;

import org.junit.Test;
import org.mockito.Mockito;
import work.soho.ai.biz.domain.AiProxyConfig;
import work.soho.ai.biz.dto.AiProxyRuntimeStateSnapshot;
import work.soho.ai.biz.service.AiProxyRuntimeStateRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    public void getStateSnapshot_shouldExposeRuntimeMetrics() {
        AiProxyRuntimeStateServiceImpl service = new AiProxyRuntimeStateServiceImpl();
        AiProxyConfig proxyConfig = buildProxyConfig(5L, 9);

        service.recordSuccess(proxyConfig.getId(), 12_000L);
        service.recordFailure(proxyConfig.getId(), new IllegalStateException("proxy connection reset"));

        AiProxyRuntimeStateSnapshot snapshot = service.getStateSnapshot(proxyConfig);

        assertThat(snapshot).isNotNull();
        assertThat(snapshot.getProxyConfigId()).isEqualTo(5L);
        assertThat(snapshot.getBaseWeight()).isEqualTo(9);
        assertThat(snapshot.getEffectiveWeight()).isBetween(1, 9);
        assertThat(snapshot.getTotalSuccessCount()).isEqualTo(1L);
        assertThat(snapshot.getTotalFailureCount()).isEqualTo(1L);
        assertThat(snapshot.getLastErrorMessage()).contains("proxy connection reset");
    }

    @Test
    public void getStateSnapshot_whenRepositoryHasSnapshot_shouldHydrateMemoryState() {
        AiProxyRuntimeStateRepository repository = Mockito.mock(AiProxyRuntimeStateRepository.class);
        AiProxyRuntimeStateServiceImpl service = new AiProxyRuntimeStateServiceImpl(repository);
        AiProxyConfig proxyConfig = buildProxyConfig(6L, 10);
        AiProxyRuntimeStateSnapshot snapshot = new AiProxyRuntimeStateSnapshot();
        snapshot.setProxyConfigId(6L);
        snapshot.setCircuitOpen(true);
        snapshot.setCircuitOpenUntilMs(System.currentTimeMillis() + 30_000L);
        snapshot.setConsecutiveFailures(2);
        snapshot.setLastErrorMessage("proxy dns timeout");
        snapshot.setTotalFailureCount(2L);
        when(repository.findById(6L)).thenReturn(java.util.Optional.of(snapshot));

        AiProxyRuntimeStateSnapshot result = service.getStateSnapshot(proxyConfig);

        assertThat(result).isNotNull();
        assertThat(result.getCircuitOpen()).isTrue();
        assertThat(service.isRequestAllowed(proxyConfig)).isFalse();
        assertThat(service.getEffectiveWeight(proxyConfig)).isZero();
    }

    @Test
    public void recordSuccess_shouldPersistSnapshotToRepository() {
        AiProxyRuntimeStateRepository repository = Mockito.mock(AiProxyRuntimeStateRepository.class);
        AiProxyRuntimeStateServiceImpl service = new AiProxyRuntimeStateServiceImpl(repository);

        service.recordSuccess(7L, 8_000L);

        verify(repository).save(Mockito.argThat(snapshot ->
                snapshot != null
                        && Long.valueOf(7L).equals(snapshot.getProxyConfigId())
                        && Long.valueOf(1L).equals(snapshot.getTotalSuccessCount())));
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
