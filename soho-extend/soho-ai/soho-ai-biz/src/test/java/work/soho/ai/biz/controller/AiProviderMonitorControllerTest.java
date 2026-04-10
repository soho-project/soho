package work.soho.ai.biz.controller;

import org.junit.Test;
import org.mockito.Mockito;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiApiCallLogProviderConfigStatsDTO;
import work.soho.ai.biz.dto.AiProviderMonitorViewDTO;
import work.soho.ai.biz.dto.AiProviderRuntimeStateSnapshotDTO;
import work.soho.ai.biz.service.AiApiCallLogService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderRuntimeStateService;
import work.soho.common.core.result.R;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * AI 提供方监控 Controller 测试。
 */
public class AiProviderMonitorControllerTest {

    @Test
    public void list_shouldMergeProviderRuntimeAndStats() {
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiApiCallLogService aiApiCallLogService = Mockito.mock(AiApiCallLogService.class);
        AiProviderRuntimeStateService aiProviderRuntimeStateService = Mockito.mock(AiProviderRuntimeStateService.class);
        AiProviderMonitorController controller = new AiProviderMonitorController(
                aiProviderConfigService,
                aiApiCallLogService,
                aiProviderRuntimeStateService
        );

        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setId(11L);
        providerConfig.setCode("openai-prod");
        providerConfig.setProvider("openai");
        providerConfig.setEnv("prod");
        providerConfig.setDefaultModel("gpt-4o-mini");
        providerConfig.setStatus(1);
        providerConfig.setWeight(8);
        when(aiProviderConfigService.list(Mockito.any())).thenReturn(Collections.singletonList(providerConfig));

        AiProviderRuntimeStateSnapshotDTO snapshot = new AiProviderRuntimeStateSnapshotDTO();
        snapshot.setProviderConfigId(11L);
        snapshot.setRequestAllowed(true);
        snapshot.setEffectiveWeight(6);
        snapshot.setLastSuccessAtMs(1000L);
        snapshot.setLastFailureAtMs(900L);
        snapshot.setEwmaTotalMs(1200L);
        snapshot.setEwmaFirstTokenMs(300L);
        snapshot.setConsecutiveFailures(1);
        snapshot.setConsecutiveSlowRequests(0);
        snapshot.setLastErrorMessage("read timed out");
        when(aiProviderRuntimeStateService.getSnapshot(providerConfig)).thenReturn(snapshot);

        when(aiApiCallLogService.statisticsTodayByProviderConfig()).thenReturn(Collections.singletonList(
                new AiApiCallLogProviderConfigStatsDTO(11L, 5L, 100L, 200L, 300L)
        ));
        when(aiApiCallLogService.statisticsTotalByProviderConfig()).thenReturn(Collections.singletonList(
                new AiApiCallLogProviderConfigStatsDTO(11L, 20L, 1000L, 2000L, 3000L)
        ));

        R<List<AiProviderMonitorViewDTO>> result = controller.list();

        assertThat(result.getPayload()).hasSize(1);
        AiProviderMonitorViewDTO view = result.getPayload().get(0);
        assertThat(view.getProviderConfigId()).isEqualTo(11L);
        assertThat(view.getProviderCode()).isEqualTo("openai-prod");
        assertThat(view.getEffectiveWeight()).isEqualTo(6);
        assertThat(view.isRequestAllowed()).isTrue();
        assertThat(view.getTodayRequestCount()).isEqualTo(5L);
        assertThat(view.getTodayTotalTokens()).isEqualTo(300L);
        assertThat(view.getTotalRequestCount()).isEqualTo(20L);
        assertThat(view.getTotalTotalTokens()).isEqualTo(3000L);
        assertThat(view.getLastErrorMessage()).isEqualTo("read timed out");
    }
}
