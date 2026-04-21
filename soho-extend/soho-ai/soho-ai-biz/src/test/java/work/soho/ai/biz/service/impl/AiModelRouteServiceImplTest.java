package work.soho.ai.biz.service.impl;

import org.junit.Test;
import org.mockito.Mockito;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiResolvedModelRoute;
import work.soho.ai.biz.service.AiModelInfoService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * 模型路由服务测试。
 */
public class AiModelRouteServiceImplTest {

    /**
     * 全局路由应支持从未绑定模型回退到可用实际模型。
     */
    @Test
    public void resolveRoute_whenAliasModelHasFallback_shouldResolveActualModel() {
        AiModelInfoService aiModelInfoService = Mockito.mock(AiModelInfoService.class);
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiModelRouteServiceImpl service = new AiModelRouteServiceImpl(
                aiModelInfoService,
                aiProviderConfigService,
                aiProviderModelRelService
        );

        AiProviderConfig providerConfig = createProviderConfig(1L, "openai-main", "gpt-4o-mini");
        when(aiProviderConfigService.listEnabledProviderConfigs()).thenReturn(List.of(providerConfig));
        when(aiProviderModelRelService.listEnabledModelsByProviderConfigId(1L)).thenReturn(Collections.emptyList());
        when(aiModelInfoService.findEnabledByModelName("alias-model")).thenReturn(createModelInfo(10L, "alias-model", 11L));
        when(aiModelInfoService.findEnabledById(11L)).thenReturn(createModelInfo(11L, "gpt-4o-mini", null));

        AiResolvedModelRoute route = service.resolveRoute("alias-model");

        assertThat(route.getRequestedModel()).isEqualTo("alias-model");
        assertThat(route.getActualModel()).isEqualTo("gpt-4o-mini");
        assertThat(route.isFallbackApplied()).isTrue();
        assertThat(route.getFallbackChain()).containsExactly("alias-model", "gpt-4o-mini");
    }

    /**
     * 指定 provider 时只能在该 provider 内解析兜底模型，不能跳到其它 provider。
     */
    @Test
    public void resolveRouteForProvider_whenFallbackOnlyExistsInOtherProvider_shouldNotCrossProvider() {
        AiModelInfoService aiModelInfoService = Mockito.mock(AiModelInfoService.class);
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiModelRouteServiceImpl service = new AiModelRouteServiceImpl(
                aiModelInfoService,
                aiProviderConfigService,
                aiProviderModelRelService
        );

        AiProviderConfig blockedProvider = createProviderConfig(1L, "anthropic-main", "claude-3-5-sonnet");
        AiProviderConfig availableProvider = createProviderConfig(2L, "openai-main", "gpt-4o-mini");
        when(aiProviderConfigService.listEnabledProviderConfigs()).thenReturn(List.of(blockedProvider, availableProvider));
        when(aiProviderModelRelService.listEnabledModelsByProviderConfigId(1L)).thenReturn(Collections.emptyList());
        when(aiProviderModelRelService.listEnabledModelsByProviderConfigId(2L)).thenReturn(Collections.emptyList());
        when(aiModelInfoService.findEnabledByModelName("alias-model")).thenReturn(createModelInfo(10L, "alias-model", 11L));
        when(aiModelInfoService.findEnabledById(11L)).thenReturn(createModelInfo(11L, "gpt-4o-mini", null));

        AiResolvedModelRoute route = service.resolveRouteForProvider(blockedProvider, "alias-model");

        assertThat(route.getRequestedModel()).isEqualTo("alias-model");
        assertThat(route.getActualModel()).isNull();
        assertThat(route.isFallbackApplied()).isFalse();
        assertThat(route.getFallbackChain()).containsExactly("alias-model", "gpt-4o-mini");
    }

    /**
     * 兜底链存在循环时应抛出清晰异常。
     */
    @Test
    public void resolveRoute_whenFallbackCycleExists_shouldThrowClearError() {
        AiModelInfoService aiModelInfoService = Mockito.mock(AiModelInfoService.class);
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiModelRouteServiceImpl service = new AiModelRouteServiceImpl(
                aiModelInfoService,
                aiProviderConfigService,
                aiProviderModelRelService
        );

        when(aiProviderConfigService.listEnabledProviderConfigs()).thenReturn(Collections.emptyList());
        when(aiModelInfoService.findEnabledByModelName("alias-a")).thenReturn(createModelInfo(1L, "alias-a", 2L));
        when(aiModelInfoService.findEnabledByModelName("alias-b")).thenReturn(createModelInfo(2L, "alias-b", 1L));
        when(aiModelInfoService.findEnabledById(1L)).thenReturn(createModelInfo(1L, "alias-a", 2L));
        when(aiModelInfoService.findEnabledById(2L)).thenReturn(createModelInfo(2L, "alias-b", 1L));

        assertThatThrownBy(() -> service.resolveRoute("alias-a"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("模型兜底配置存在循环: alias-a -> alias-b -> alias-a");
    }

    /**
     * 展示模型列表应包含可路由到当前 provider 的别名模型。
     */
    @Test
    public void listDisplayModelsByProvider_whenAliasCanFallback_shouldContainAliasModel() {
        AiModelInfoService aiModelInfoService = Mockito.mock(AiModelInfoService.class);
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiModelRouteServiceImpl service = new AiModelRouteServiceImpl(
                aiModelInfoService,
                aiProviderConfigService,
                aiProviderModelRelService
        );

        AiProviderConfig providerConfig = createProviderConfig(1L, "openai-main", "gpt-4o-mini");
        when(aiProviderModelRelService.listEnabledModelsByProviderConfigId(1L)).thenReturn(Collections.emptyList());
        when(aiModelInfoService.listEnabledModels()).thenReturn(List.of(
                createModelInfo(11L, "gpt-4o-mini", null),
                createModelInfo(10L, "alias-model", 11L)
        ));
        when(aiModelInfoService.findEnabledByModelName("alias-model")).thenReturn(createModelInfo(10L, "alias-model", 11L));
        when(aiModelInfoService.findEnabledByModelName("gpt-4o-mini")).thenReturn(createModelInfo(11L, "gpt-4o-mini", null));
        when(aiModelInfoService.findEnabledById(11L)).thenReturn(createModelInfo(11L, "gpt-4o-mini", null));

        List<String> models = service.listDisplayModelsByProvider(providerConfig);

        assertThat(models).containsExactly("gpt-4o-mini", "alias-model");
    }

    /**
     * 构造模型信息测试数据。
     */
    private AiModelInfo createModelInfo(Long id, String modelName, Long fallbackModelId) {
        AiModelInfo modelInfo = new AiModelInfo();
        modelInfo.setId(id);
        modelInfo.setModelName(modelName);
        modelInfo.setFallbackModelId(fallbackModelId);
        return modelInfo;
    }

    /**
     * 构造 provider 测试数据。
     */
    private AiProviderConfig createProviderConfig(Long id, String code, String supportedModel) {
        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setId(id);
        providerConfig.setCode(code);
        providerConfig.setDefaultModel(supportedModel);
        providerConfig.setSupportedModels(supportedModel);
        return providerConfig;
    }
}
