package work.soho.ai.biz.service.impl;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.request.OpenAiResponsesRequest;
import work.soho.ai.biz.service.AiFileService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.common.core.util.JacksonUtils;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class AiChatServiceImplTest {

    @Test
    public void streamChat_whenProviderNotStreaming_wrapsIntoOpenAiStream() {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiChatServiceImpl service = Mockito.spy(new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService));

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

        System.out.println( items);
    }

    @Test
    public void streamChat_whenCodexAdapter_parsesDeltaToOpenAiPayload() {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiChatServiceImpl service = Mockito.spy(new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService));

        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setStatus(1);
        providerConfig.setProvider("openai");
        providerConfig.setCode("codex");
        providerConfig.setBaseUrl("https://example.com");
        providerConfig.setApiKeyRef("token");
        providerConfig.setDefaultModel("gpt-5-codex");
        providerConfig.setConfigJson("{\"adapter\":\"codexResponses\"}");

        String sse = "data: {\"type\":\"response.output_text.delta\",\"delta\":\"hi\"}\n\n"
                + "data: {\"type\":\"response.completed\"}\n\n";
        DataBuffer buffer = new DefaultDataBufferFactory().wrap(sse.getBytes(StandardCharsets.UTF_8));

        ExchangeFunction exchange = request -> {
            ClientResponse response = ClientResponse.create(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_EVENT_STREAM_VALUE)
                    .body(Flux.just(buffer))
                    .build();
            return reactor.core.publisher.Mono.just(response);
        };

        WebClient webClient = WebClient.builder().exchangeFunction(exchange).build();
        doReturn(webClient).when(service).buildWebClient();

        AiChatRequest request = new AiChatRequest();
        request.setProviderCode("codex");
        request.setInput("hello");

        List<String> items = service.streamChat(providerConfig, request).collectList().block();

        assertThat(items).hasSize(2);
        assertThat(items.get(0)).contains("\"chat.completion.chunk\"");
        assertThat(items.get(0)).contains("\"hi\"");
        assertThat(items.get(1)).isEqualTo("[DONE]");
    }

    @Test
    public void resolveProviderConfig_whenModelHasMultipleProviders_shouldRouteByWeight() {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiChatServiceImpl service = new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService);

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

        when(providerConfigService.list(Mockito.any())).thenReturn(Arrays.asList(lowWeightConfig, highWeightConfig));

        for (int i = 0; i < 20; i++) {
            AiProviderConfig selected = service.resolveProviderConfig(null, "gpt-4o-mini");
            assertThat(selected.getId()).isEqualTo(2L);
        }
    }

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

    @Test
    public void resolveCodexRequestBody_whenNativeResponsesRequest_shouldKeepOriginalTools() throws Exception {
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService providerModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiFileService aiFileService = Mockito.mock(AiFileService.class);
        AiChatServiceImpl service = new AiChatServiceImpl(providerConfigService, providerModelRelService, aiFileService);

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
}
