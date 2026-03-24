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
import work.soho.ai.biz.service.AiFileService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

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
}
