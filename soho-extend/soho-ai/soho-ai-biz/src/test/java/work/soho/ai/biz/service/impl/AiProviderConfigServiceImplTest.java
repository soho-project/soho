package work.soho.ai.biz.service.impl;

import org.junit.Test;
import org.mockito.Mockito;
import work.soho.ai.biz.domain.AiProviderConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AiProviderConfigServiceImplTest {

    @Test
    public void listEnabledProviderConfigs_whenCalledTwiceWithinTtl_shouldReuseLocalCache() {
        AiProviderConfigServiceImpl service = Mockito.spy(new AiProviderConfigServiceImpl());

        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setId(1L);
        providerConfig.setCode("openai-prod");
        providerConfig.setStatus(1);

        doReturn(List.of(providerConfig)).when(service).loadEnabledProviderConfigsForCache();

        List<AiProviderConfig> first = service.listEnabledProviderConfigs();
        List<AiProviderConfig> second = service.listEnabledProviderConfigs();

        assertThat(first).hasSize(1);
        assertThat(second).hasSize(1);
        verify(service, times(1)).loadEnabledProviderConfigsForCache();
    }

    @Test
    public void clearEnabledProviderCaches_shouldForceReload() {
        AiProviderConfigServiceImpl service = Mockito.spy(new AiProviderConfigServiceImpl());

        AiProviderConfig firstConfig = new AiProviderConfig();
        firstConfig.setId(1L);
        firstConfig.setCode("openai-prod");
        firstConfig.setStatus(1);

        AiProviderConfig secondConfig = new AiProviderConfig();
        secondConfig.setId(2L);
        secondConfig.setCode("gemini-prod");
        secondConfig.setStatus(1);

        doReturn(List.of(firstConfig), List.of(secondConfig)).when(service).loadEnabledProviderConfigsForCache();

        List<AiProviderConfig> first = service.listEnabledProviderConfigs();
        service.clearEnabledProviderCaches();
        List<AiProviderConfig> second = service.listEnabledProviderConfigs();

        assertThat(first).extracting(AiProviderConfig::getCode).containsExactly("openai-prod");
        assertThat(second).extracting(AiProviderConfig::getCode).containsExactly("gemini-prod");
        verify(service, times(2)).loadEnabledProviderConfigsForCache();
    }
}
