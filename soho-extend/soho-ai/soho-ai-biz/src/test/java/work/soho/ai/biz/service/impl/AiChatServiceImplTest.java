package work.soho.ai.biz.service.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.dto.AiUsageSummary;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.request.OpenAiResponsesRequest;
import work.soho.ai.biz.service.AiFileService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.ai.biz.service.AiProviderRuntimeStateService;
import work.soho.ai.biz.service.AiProxyConfigService;
import work.soho.ai.biz.service.AiProxyRelayService;
import work.soho.ai.biz.service.AiProxyRuntimeStateService;
import work.soho.ai.biz.service.AiUpstreamClientFactory;
import work.soho.ai.biz.utils.AiProxyLayerUtils;
import work.soho.common.core.util.JacksonUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 聊天服务测试。
 */
public class AiChatServiceImplTest {

    /**
     * 非流式上游应包装为 OpenAI 流。
     */
    @Test
    public void streamChat_whenProviderNotStreaming_wrapsIntoOpenAiStream() {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiProviderRuntimeStateService runtimeStateService = allowAllRuntimeStateService();
        AiChatServiceImpl service = Mockito.spy(new AiChatServiceImpl(
                providerConfigService,
                providerModelRelService,
                aiFileService,
                Mockito.mock(AiProxyConfigService.class),
                Mockito.mock(AiProxyRelayService.class),
                Mockito.mock(AiProxyRuntimeStateService.class),
                runtimeStateService,
                Mockito.mock(AiUpstreamClientFactory.class)
        ));

        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setStatus(1);
        providerConfig.setProvider("openai");
        providerConfig.setCode("p1");
        providerConfig.setBaseUrl("https://example.com");
        providerConfig.setApiKeyRef("token");
        providerConfig.setDefaultModel("gpt-4o-mini");
        providerConfig.setConfigJson("{\"streamSupported\":false}");

        AiChatRequest request = new AiChatRequest();
        request.setProviderCode("p1");
        request.setInput("hello");

        AiChatResponse response = new AiChatResponse();
        response.setContent("hello");
        doReturn(response).when(service).chat(providerConfig, request);

        List<String> items = service.streamChat(providerConfig, request).collectList().block();

        assertThat(items).hasSize(2);
        assertThat(items.get(0)).contains("\"choices\"");
        assertThat(items.get(0)).contains("hello");
        assertThat(items.get(1)).isEqualTo("[DONE]");
    }

    /**
     * Codex 适配流应通过共享工厂请求并转换为 OpenAI chunk。
     */
    @Test
    public void streamChat_whenCodexAdapter_parsesDeltaToOpenAiPayload() {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiProxyConfigService aiProxyConfigService = Mockito.mock(AiProxyConfigService.class);
        AiProxyRelayService aiProxyRelayService = Mockito.mock(AiProxyRelayService.class);
        AiProviderRuntimeStateService runtimeStateService = allowAllRuntimeStateService();
        AiUpstreamClientFactory factory = Mockito.mock(AiUpstreamClientFactory.class);
        AiChatServiceImpl service = Mockito.spy(new AiChatServiceImpl(
                providerConfigService,
                providerModelRelService,
                aiFileService,
                aiProxyConfigService,
                aiProxyRelayService,
                Mockito.mock(AiProxyRuntimeStateService.class),
                runtimeStateService,
                factory
        ));
        when(aiProxyRelayService.ensureRelay(Mockito.nullable(AiProxyLayerUtils.ProxySettings.class), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setStatus(1);
        providerConfig.setProvider("openai");
        providerConfig.setCode("codex");
        providerConfig.setBaseUrl("https://example.com");
        providerConfig.setApiKeyRef("token");
        providerConfig.setDefaultModel("gpt-5-codex");
        providerConfig.setConfigJson("{\"adapter\":\"codexResponses\"}");

        when(factory.exchangeStream(anyString(), eq(HttpMethod.POST), any(HttpHeaders.class), any(), anyInt(), Mockito.nullable(AiProxyLayerUtils.ProxySettings.class)))
                .thenReturn(Flux.just(
                        "data: {\"type\":\"response.output_text.delta\",\"delta\":\"hi\"}\n\n",
                        "data: {\"type\":\"response.completed\"}\n\n"
                ));

        AiChatRequest request = new AiChatRequest();
        request.setProviderCode("codex");
        request.setInput("hello");

        List<String> items = service.streamChat(providerConfig, request).collectList().block();

        assertThat(items).hasSize(2);
        assertThat(items.get(0)).contains("\"chat.completion.chunk\"");
        assertThat(items.get(0)).contains("\"hi\"");
        assertThat(items.get(1)).isEqualTo("[DONE]");
        verify(factory, times(1)).exchangeStream(anyString(), eq(HttpMethod.POST), any(HttpHeaders.class), any(), anyInt(), Mockito.nullable(AiProxyLayerUtils.ProxySettings.class));
    }

    /**
     * 同模型多供应商时应按权重路由。
     */
    @Test
    public void resolveProviderConfig_whenModelHasMultipleProviders_shouldRouteByWeight() {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiProviderRuntimeStateService runtimeStateService = Mockito.mock(AiProviderRuntimeStateService.class);
        when(runtimeStateService.isRequestAllowed(Mockito.any())).thenReturn(true);
        when(runtimeStateService.getEffectiveWeight(Mockito.argThat(config -> config != null && Long.valueOf(1L).equals(config.getId()))))
                .thenReturn(0);
        when(runtimeStateService.getEffectiveWeight(Mockito.argThat(config -> config != null && Long.valueOf(2L).equals(config.getId()))))
                .thenReturn(10);
        AiChatServiceImpl service = new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService,
                Mockito.mock(AiProxyConfigService.class), Mockito.mock(AiProxyRelayService.class),
                Mockito.mock(AiProxyRuntimeStateService.class), runtimeStateService, Mockito.mock(AiUpstreamClientFactory.class));

        when(providerModelRelService.listEnabledProviderConfigIdsByModelName("gpt-4o-mini"))
                .thenReturn(Arrays.asList(1L, 2L));

        AiProviderConfig lowWeightConfig = new AiProviderConfig();
        lowWeightConfig.setId(1L);
        lowWeightConfig.setStatus(1);
        lowWeightConfig.setCode("low");
        lowWeightConfig.setWeight(0);

        AiProviderConfig highWeightConfig = new AiProviderConfig();
        highWeightConfig.setId(2L);
        highWeightConfig.setStatus(1);
        highWeightConfig.setCode("high");
        highWeightConfig.setWeight(10);

        when(providerConfigService.listEnabledProviderConfigs()).thenReturn(Arrays.asList(lowWeightConfig, highWeightConfig));

        for (int i = 0; i < 20; i++) {
            AiProviderConfig selected = service.resolveProviderConfig(null, "gpt-4o-mini");
            assertThat(selected.getId()).isEqualTo(2L);
        }
    }

    /**
     * 没有关联表时，声明支持模型的供应商仍应参与选择。
     */
    @Test
    public void resolveProviderConfig_whenProviderHasNoRelationButDeclaresModel_shouldStillParticipate() {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiProviderRuntimeStateService runtimeStateService = Mockito.mock(AiProviderRuntimeStateService.class);
        when(runtimeStateService.isRequestAllowed(Mockito.any())).thenReturn(true);
        when(runtimeStateService.getEffectiveWeight(Mockito.argThat(config -> config != null && Long.valueOf(9L).equals(config.getId()))))
                .thenReturn(10);
        AiChatServiceImpl service = new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService,
                Mockito.mock(AiProxyConfigService.class), Mockito.mock(AiProxyRelayService.class),
                Mockito.mock(AiProxyRuntimeStateService.class), runtimeStateService, Mockito.mock(AiUpstreamClientFactory.class));

        when(providerModelRelService.listEnabledProviderConfigIdsByModelName("gpt-4o-mini"))
                .thenReturn(Arrays.asList());

        AiProviderConfig config = new AiProviderConfig();
        config.setId(9L);
        config.setStatus(1);
        config.setCode("fallback");
        config.setWeight(10);
        config.setDefaultModel("gpt-4o-mini");
        config.setSupportedModels("gpt-4o-mini\ngpt-4.1");

        when(providerConfigService.listEnabledProviderConfigs()).thenReturn(Arrays.asList(config));

        AiProviderConfig selected = service.resolveProviderConfig(null, "gpt-4o-mini");
        assertThat(selected.getId()).isEqualTo(9L);
    }

    /**
     * 指定 provider 时应只选对应供应商。
     */
    @Test
    public void resolveProviderConfigByProvider_whenSameModelHasMultipleProviders_shouldOnlySelectSpecifiedProvider() {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiProviderRuntimeStateService runtimeStateService = Mockito.mock(AiProviderRuntimeStateService.class);
        when(runtimeStateService.isRequestAllowed(Mockito.any())).thenReturn(true);
        when(runtimeStateService.getEffectiveWeight(Mockito.any())).thenReturn(10);
        AiChatServiceImpl service = new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService,
                Mockito.mock(AiProxyConfigService.class), Mockito.mock(AiProxyRelayService.class),
                Mockito.mock(AiProxyRuntimeStateService.class), runtimeStateService, Mockito.mock(AiUpstreamClientFactory.class));

        when(providerModelRelService.listEnabledProviderConfigIdsByModelName("gpt-4o-mini"))
                .thenReturn(Arrays.asList(1L, 2L));

        AiProviderConfig openaiConfig = new AiProviderConfig();
        openaiConfig.setId(1L);
        openaiConfig.setStatus(1);
        openaiConfig.setCode("openai-prod");
        openaiConfig.setProvider("openai");
        openaiConfig.setWeight(10);

        AiProviderConfig geminiConfig = new AiProviderConfig();
        geminiConfig.setId(2L);
        geminiConfig.setStatus(1);
        geminiConfig.setCode("gemini-prod");
        geminiConfig.setProvider("gemini");
        geminiConfig.setWeight(10);

        when(providerConfigService.listEnabledProviderConfigs()).thenReturn(Arrays.asList(openaiConfig, geminiConfig));

        AiProviderConfig selected = service.resolveProviderConfigByProvider("gemini", "gpt-4o-mini");
        assertThat(selected.getId()).isEqualTo(2L);
        assertThat(selected.getProvider()).isEqualTo("gemini");

        assertThatThrownBy(() -> service.resolveProviderConfigByProvider("anthropic", "gpt-4o-mini"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("provider config not found for provider: anthropic, model: gpt-4o-mini");
    }

    /**
     * 原始请求里的 parameters 字段应保留。
     */
    @Test
    public void openAiResponsesRequest_whenRawRequestUsesParameters_shouldKeepParametersField() {
        String raw = "{"
                + "\"model\":\"gpt-5.4\","
                + "\"stream\":true,"
                + "\"tools\":[{"
                + "\"type\":\"function\","
                + "\"name\":\"exec_command\","
                + "\"description\":\"Runs a command\","
                + "\"strict\":false,"
                + "\"parameters\":{"
                + "\"type\":\"object\","
                + "\"properties\":{"
                + "\"cmd\":{\"type\":\"string\"}"
                + "},"
                + "\"required\":[\"cmd\"],"
                + "\"additionalProperties\":false"
                + "}"
                + "}]"
                + "}";

        OpenAiResponsesRequest request = JacksonUtils.toBean(raw, OpenAiResponsesRequest.class);
        String json = JacksonUtils.toJson(request);

        assertThat(request.getTools()).hasSize(1);
        assertThat(request.getTools().get(0).get("parameters")).isNotNull();
        assertThat(json).contains("\"parameters\"");
        assertThat(json).doesNotContain("\"input_schema\"");
    }

    /**
     * custom tool 的 format 字段应保留。
     */
    @Test
    public void openAiResponsesRequest_whenRawRequestUsesCustomTool_shouldKeepCustomFormat() {
        String raw = "{"
                + "\"model\":\"gpt-5.4\","
                + "\"stream\":true,"
                + "\"tools\":[{"
                + "\"type\":\"custom\","
                + "\"name\":\"apply_patch\","
                + "\"description\":\"Patch files\","
                + "\"format\":{"
                + "\"type\":\"grammar\","
                + "\"syntax\":\"lark\","
                + "\"definition\":\"start: begin_patch\""
                + "}"
                + "}]"
                + "}";

        OpenAiResponsesRequest request = JacksonUtils.toBean(raw, OpenAiResponsesRequest.class);
        String json = JacksonUtils.toJson(request);

        assertThat(request.getTools()).hasSize(1);
        assertThat(request.getTools().get(0).get("format")).isNotNull();
        assertThat(json).contains("\"format\"");
        assertThat(json).doesNotContain("\"parameters\":null");
    }

    /**
     * native responses 请求体应保留原始 tools 定义。
     */
    @Test
    public void resolveCodexRequestBody_whenNativeResponsesRequest_shouldKeepOriginalTools() throws Exception {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiProviderRuntimeStateService runtimeStateService = allowAllRuntimeStateService();
        AiChatServiceImpl service = new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService,
                Mockito.mock(AiProxyConfigService.class), Mockito.mock(AiProxyRelayService.class),
                Mockito.mock(AiProxyRuntimeStateService.class), runtimeStateService, Mockito.mock(AiUpstreamClientFactory.class));

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-5.4");
        body.put("stream", true);
        body.put("tools", List.of(
                Map.of(
                        "type", "function",
                        "name", "exec_command",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of("cmd", Map.of("type", "string"))
                        )
                ),
                Map.of(
                        "type", "custom",
                        "name", "apply_patch",
                        "format", Map.of(
                                "type", "grammar",
                                "syntax", "lark",
                                "definition", "start: begin_patch"
                        )
                )
        ));

        AiChatRequest request = new AiChatRequest();
        request.setExtra(Map.of("nativeResponses", true, "responsesRequestBody", body));

        Method method = AiChatServiceImpl.class.getDeclaredMethod(
                "resolveCodexRequestBody", String.class, List.class, AiChatRequest.class, Map.class, boolean.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> resolved = (Map<String, Object>) method.invoke(
                service, "gpt-5.4", null, request, new HashMap<>(), true);
        String json = JacksonUtils.toJson(resolved);

        assertThat(json).contains("\"parameters\"");
        assertThat(json).contains("\"format\"");
        assertThat(json).doesNotContain("\"parameters\":null");
    }

    /**
     * 首个供应商不可用时应跳过到下一个。
     */
    @Test
    public void resolveProviderConfig_whenFirstProviderUnavailable_shouldSkipToNextAvailable() {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiProviderRuntimeStateService runtimeStateService = Mockito.mock(AiProviderRuntimeStateService.class);
        AiChatServiceImpl service = new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService,
                Mockito.mock(AiProxyConfigService.class), Mockito.mock(AiProxyRelayService.class),
                Mockito.mock(AiProxyRuntimeStateService.class), runtimeStateService, Mockito.mock(AiUpstreamClientFactory.class));

        when(providerModelRelService.listEnabledProviderConfigIdsByModelName("gpt-4o-mini"))
                .thenReturn(Arrays.asList(1L, 2L));

        AiProviderConfig first = new AiProviderConfig();
        first.setId(1L);
        first.setCode("p1");
        first.setStatus(1);

        AiProviderConfig second = new AiProviderConfig();
        second.setId(2L);
        second.setCode("p2");
        second.setStatus(1);

        when(providerConfigService.listEnabledProviderConfigs()).thenReturn(Arrays.asList(first, second));
        when(runtimeStateService.isRequestAllowed(first)).thenReturn(false);
        when(runtimeStateService.isRequestAllowed(second)).thenReturn(true);
        when(runtimeStateService.getEffectiveWeight(second)).thenReturn(5);

        AiProviderConfig selected = service.resolveProviderConfig(null, "gpt-4o-mini");

        assertThat(selected.getCode()).isEqualTo("p2");
    }

    /**
     * 所有供应商不可用时应抛出清晰错误。
     */
    @Test
    public void resolveProviderConfig_whenAllProvidersUnavailable_shouldThrowClearError() {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiProviderRuntimeStateService runtimeStateService = Mockito.mock(AiProviderRuntimeStateService.class);
        AiChatServiceImpl service = new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService,
                Mockito.mock(AiProxyConfigService.class), Mockito.mock(AiProxyRelayService.class),
                Mockito.mock(AiProxyRuntimeStateService.class), runtimeStateService, Mockito.mock(AiUpstreamClientFactory.class));

        when(providerModelRelService.listEnabledProviderConfigIdsByModelName("gpt-4o-mini"))
                .thenReturn(Arrays.asList(1L, 2L));

        AiProviderConfig first = new AiProviderConfig();
        first.setId(1L);
        first.setCode("p1");
        first.setStatus(1);

        AiProviderConfig second = new AiProviderConfig();
        second.setId(2L);
        second.setCode("p2");
        second.setStatus(1);

        when(providerConfigService.listEnabledProviderConfigs()).thenReturn(Arrays.asList(first, second));
        when(runtimeStateService.isRequestAllowed(first)).thenReturn(false);
        when(runtimeStateService.isRequestAllowed(second)).thenReturn(false);

        assertThatThrownBy(() -> service.resolveProviderConfig(null, "gpt-4o-mini"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("all upstream providers are temporarily unavailable for model: gpt-4o-mini");
    }

    /**
     * Anthropic usage 应把 cache token 合并到输入 token。
     */
    @Test
    public void extractUsage_whenAnthropicHasCacheTokens_shouldMergeIntoPromptTokens() throws Exception {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiProviderRuntimeStateService runtimeStateService = allowAllRuntimeStateService();
        AiChatServiceImpl service = new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService,
                Mockito.mock(AiProxyConfigService.class), Mockito.mock(AiProxyRelayService.class),
                Mockito.mock(AiProxyRuntimeStateService.class), runtimeStateService, Mockito.mock(AiUpstreamClientFactory.class));

        Method method = AiChatServiceImpl.class.getDeclaredMethod("extractUsage", String.class, String.class);
        method.setAccessible(true);
        AiUsageSummary usage = (AiUsageSummary) method.invoke(service, "anthropic", "{\"usage\":{\"input_tokens\":100,\"cache_creation_input_tokens\":20,\"cache_read_input_tokens\":30,\"output_tokens\":40}}");

        assertThat(usage.getPromptTokens()).isEqualTo(150);
        assertThat(usage.getCompletionTokens()).isEqualTo(40);
        assertThat(usage.getTotalTokens()).isEqualTo(190);
        assertThat(usage.getCachedInputTokens()).isEqualTo(50);
        assertThat(usage.getCacheCreationInputTokens()).isEqualTo(20);
        assertThat(usage.getCacheReadInputTokens()).isEqualTo(30);
    }

    /**
     * OpenAI usage 应保留 cached tokens 明细但不重复累加主 token。
     */
    @Test
    public void extractUsage_whenOpenAiHasCachedTokens_shouldKeepDetailsOnly() throws Exception {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiProviderRuntimeStateService runtimeStateService = allowAllRuntimeStateService();
        AiChatServiceImpl service = new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService,
                Mockito.mock(AiProxyConfigService.class), Mockito.mock(AiProxyRelayService.class),
                Mockito.mock(AiProxyRuntimeStateService.class), runtimeStateService, Mockito.mock(AiUpstreamClientFactory.class));

        Method method = AiChatServiceImpl.class.getDeclaredMethod("extractUsage", String.class, String.class);
        method.setAccessible(true);
        AiUsageSummary usage = (AiUsageSummary) method.invoke(service, "openai", "{\"usage\":{\"prompt_tokens\":120,\"completion_tokens\":80,\"total_tokens\":200,\"prompt_tokens_details\":{\"cached_tokens\":35}}}");

        assertThat(usage.getPromptTokens()).isEqualTo(120);
        assertThat(usage.getCompletionTokens()).isEqualTo(80);
        assertThat(usage.getTotalTokens()).isEqualTo(200);
        assertThat(usage.getCachedInputTokens()).isEqualTo(35);
        assertThat(usage.getCacheCreationInputTokens()).isEqualTo(0);
        assertThat(usage.getCacheReadInputTokens()).isEqualTo(0);
    }

    /**
     * 非流式 JSON 请求应复用上游工厂。
     */
    @Test
    public void postJson_shouldReuseUpstreamClientFactory() throws Exception {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiProviderRuntimeStateService runtimeStateService = allowAllRuntimeStateService();
        AiUpstreamClientFactory factory = Mockito.mock(AiUpstreamClientFactory.class);
        AiChatServiceImpl service = Mockito.spy(new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService,
                Mockito.mock(AiProxyConfigService.class), Mockito.mock(AiProxyRelayService.class),
                Mockito.mock(AiProxyRuntimeStateService.class), runtimeStateService, factory));
        when(factory.exchangeJson(anyString(), eq(HttpMethod.POST), any(HttpHeaders.class), any(), anyInt(), Mockito.nullable(AiProxyLayerUtils.ProxySettings.class)))
                .thenReturn(ResponseEntity.ok("{\"ok\":true}"));

        Method method = AiChatServiceImpl.class.getDeclaredMethod("postJson", String.class, Map.class, Map.class, Integer.class, Map.class);
        method.setAccessible(true);
        String response = (String) method.invoke(service, "https://example.com", Map.of("Authorization", "Bearer x"), Map.of("model", "gpt"), 1234, new HashMap<>());

        assertThat(response).isEqualTo("{\"ok\":true}");
        verify(factory, times(1)).exchangeJson(anyString(), eq(HttpMethod.POST), any(HttpHeaders.class), any(), anyInt(), Mockito.nullable(AiProxyLayerUtils.ProxySettings.class));
    }

    /**
     * OpenAI 流式请求应通过共享上游工厂发起。
     */
    @Test
    public void streamOpenAiCompatible_shouldReuseUpstreamClientFactory() throws Exception {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiProxyConfigService aiProxyConfigService = Mockito.mock(AiProxyConfigService.class);
        AiProxyRelayService aiProxyRelayService = Mockito.mock(AiProxyRelayService.class);
        AiProviderRuntimeStateService runtimeStateService = allowAllRuntimeStateService();
        AiUpstreamClientFactory factory = Mockito.mock(AiUpstreamClientFactory.class);
        AiChatServiceImpl service = Mockito.spy(new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService,
                aiProxyConfigService, aiProxyRelayService, Mockito.mock(AiProxyRuntimeStateService.class), runtimeStateService, factory));
        when(aiProxyRelayService.ensureRelay(Mockito.nullable(AiProxyLayerUtils.ProxySettings.class), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(factory.exchangeStream(anyString(), eq(HttpMethod.POST), any(HttpHeaders.class), any(), anyInt(), Mockito.nullable(AiProxyLayerUtils.ProxySettings.class)))
                .thenReturn(Flux.just(
                        "data: {\"choices\":[{\"delta\":{\"content\":\"hi\"}}]}\n\n",
                        "data: [DONE]\n\n"
                ));

        Method method = AiChatServiceImpl.class.getDeclaredMethod(
                "streamOpenAiCompatible", String.class, String.class, String.class, List.class, AiChatRequest.class, Map.class);
        method.setAccessible(true);

        AiChatRequest request = new AiChatRequest();
        request.setTopP(0.8D);
        request.setMaxTokens(128);

        AiChatRequest.Message message = new AiChatRequest.Message();
        message.setRole("user");
        message.setContent("hello");

        @SuppressWarnings("unchecked")
        Flux<String> flux = (Flux<String>) method.invoke(
                service,
                "https://example.com",
                "token-1",
                "gpt-4o-mini",
                List.of(message),
                request,
                new HashMap<>()
        );
        List<String> payloads = flux.collectList().block();

        assertThat(payloads).containsExactly(
                "{\"choices\":[{\"delta\":{\"content\":\"hi\"}}]}",
                "[DONE]"
        );
        verify(factory, times(1)).exchangeStream(
                anyString(),
                eq(HttpMethod.POST),
                Mockito.argThat(headers -> headers != null
                        && "Bearer token-1".equals(headers.getFirst(HttpHeaders.AUTHORIZATION))
                        && MediaType.APPLICATION_JSON.equals(headers.getContentType())),
                any(),
                anyInt(),
                Mockito.nullable(AiProxyLayerUtils.ProxySettings.class)
        );
    }

    /**
     * 构造默认放行的运行态服务桩。
     */
    private AiProviderRuntimeStateService allowAllRuntimeStateService() {
        AiProviderRuntimeStateService runtimeStateService = Mockito.mock(AiProviderRuntimeStateService.class);
        when(runtimeStateService.isRequestAllowed(Mockito.any())).thenReturn(true);
        when(runtimeStateService.getEffectiveWeight(Mockito.any())).thenAnswer(invocation -> {
            AiProviderConfig providerConfig = invocation.getArgument(0);
            if (providerConfig == null || providerConfig.getWeight() == null) {
                return 1;
            }
            return Math.max(providerConfig.getWeight(), 0);
        });
        return runtimeStateService;
    }
}
