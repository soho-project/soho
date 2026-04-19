package work.soho.ai.biz.service.impl;

import org.junit.Test;
import org.mockito.Mockito;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.service.AiModelInfoService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AiProviderModelRelServiceImplTest {

    @Test
    public void listEnabledModelsByProviderConfigId_whenCalledTwiceWithinTtl_shouldReuseLocalCache() {
        AiModelInfoService modelInfoService = Mockito.mock(AiModelInfoService.class);
        AiProviderModelRelServiceImpl service = Mockito.spy(new AiProviderModelRelServiceImpl(modelInfoService));
        service.setLocalCacheEnabledForTest(true);
        service.setLocalCacheTtlMsForTest(60_000L);

        AiModelInfo modelInfo = new AiModelInfo();
        modelInfo.setId(11L);
        modelInfo.setModelName("gpt-4o-mini");
        modelInfo.setStatus(1);

        doReturn(List.of(modelInfo)).when(service).loadEnabledModelsByProviderConfigIdForCache(1L);

        List<AiModelInfo> first = service.listEnabledModelsByProviderConfigId(1L);
        List<AiModelInfo> second = service.listEnabledModelsByProviderConfigId(1L);

        assertThat(first).hasSize(1);
        assertThat(second).hasSize(1);
        assertThat(service.localModelListCacheSizeForTest()).isEqualTo(1);
        verify(service, times(1)).loadEnabledModelsByProviderConfigIdForCache(1L);
        verify(modelInfoService, times(0)).getById(11L);
    }

    @Test
    public void replaceRelations_shouldInvalidateAffectedCaches() {
        AiModelInfoService modelInfoService = Mockito.mock(AiModelInfoService.class);
        AiProviderModelRelServiceImpl service = Mockito.spy(new AiProviderModelRelServiceImpl(modelInfoService));
        service.setLocalCacheEnabledForTest(true);
        service.setLocalCacheTtlMsForTest(60_000L);

        AiModelInfo oldModel = new AiModelInfo();
        oldModel.setId(11L);
        oldModel.setModelName("gpt-old");
        oldModel.setStatus(1);

        AiModelInfo newModel = new AiModelInfo();
        newModel.setId(12L);
        newModel.setModelName("gpt-new");
        newModel.setStatus(1);

        doReturn(List.of(oldModel), List.of(newModel)).when(service).loadEnabledModelsByProviderConfigIdForCache(1L);
        doReturn(List.of(1L)).when(service).loadEnabledProviderConfigIdsByModelNameForCache("gpt-old");
        when(modelInfoService.list(Mockito.any())).thenReturn(List.of(newModel));
        doReturn(true).when(service).remove(Mockito.any());
        doReturn(true).when(service).saveBatch(Mockito.any());

        service.listEnabledModelsByProviderConfigId(1L);
        service.listEnabledProviderConfigIdsByModelName("gpt-old");
        assertThat(service.localModelListCacheSizeForTest()).isEqualTo(1);
        assertThat(service.localProviderIdCacheSizeForTest()).isEqualTo(1);

        service.replaceRelations(1L, List.of(12L));

        assertThat(service.localModelCacheSnapshotForTest()).doesNotContainKey(1L);
        assertThat(service.localProviderIdCacheSnapshotForTest()).doesNotContainKey("gpt-old");
        assertThat(service.localProviderIdCacheSnapshotForTest()).doesNotContainKey("gpt-new");
    }
}
