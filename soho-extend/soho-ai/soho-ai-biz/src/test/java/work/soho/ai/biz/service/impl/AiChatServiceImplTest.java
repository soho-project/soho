package work.soho.ai.biz.service.impl;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.request.AiChatRequest;
import work.soho.ai.biz.service.AiAppService;
import work.soho.ai.biz.service.AiChatService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.test.TestApp;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
@TestPropertySource(properties = {
        "spring.mail.username=test@example.com"
})
@Log4j2
@AutoConfigureMockMvc
class AiChatServiceImplTest {
    @Autowired
    private AiAppService aiAppService;

    @Autowired
    private AiProviderConfigService providerConfigService;

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private AiChatService aiChatService;

    @Test
    void streamChat_whenProviderNotStreaming_wrapsIntoOpenAiStream() {
        AiAppService aiAppService = Mockito.mock(AiAppService.class);
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);

        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setStatus(1);
        providerConfig.setProvider("openai");
        providerConfig.setCode("p1");
        providerConfig.setConfigJson("{\"streamSupported\":false}");

        when(providerConfigService.getOne(any())).thenReturn(providerConfig);

        AiChatServiceImpl service = Mockito.spy(new AiChatServiceImpl(aiAppService, providerConfigService));

        AiChatRequest request = new AiChatRequest();
        request.setProviderCode("p1");
        request.setInput("hello");

        AiChatResponse response = new AiChatResponse();
        response.setContent("hello");
        doReturn(response).when(service).chat(request);

        List<String> items = service.streamChat(request).collectList().block();

        assertThat(items).hasSize(2);
        assertThat(items.get(0)).contains("\"choices\"");
        assertThat(items.get(0)).contains("hello");
        assertThat(items.get(1)).isEqualTo("[DONE]");
    }

    @Test
    void streamChat_whenProviderNotStreaming_emptyContent_returnsDoneOnly() {
        AiAppService aiAppService = Mockito.mock(AiAppService.class);
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);

        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setStatus(1);
        providerConfig.setProvider("openai");
        providerConfig.setCode("p2");
        providerConfig.setConfigJson("{\"streamSupported\":false}");

        when(providerConfigService.getOne(any())).thenReturn(providerConfig);

        AiChatServiceImpl service = Mockito.spy(new AiChatServiceImpl(aiAppService, providerConfigService));

        AiChatRequest request = new AiChatRequest();
        request.setProviderCode("p2");
        request.setInput("hello");

        AiChatResponse response = new AiChatResponse();
        response.setContent("");
        doReturn(response).when(service).chat(request);

        Flux<String> flux = service.streamChat(request);
        List<String> items = flux.collectList().block();

        assertThat(items).containsExactly("[DONE]");
    }

    @Test
    void streamChat_whenStreamingEnabled_parsesSsePayloads() {
        AiAppService aiAppService = Mockito.mock(AiAppService.class);
        AiProviderConfigService providerConfigService = Mockito.mock(AiProviderConfigService.class);

        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setStatus(1);
        providerConfig.setProvider("openai");
        providerConfig.setCode("p3");
        providerConfig.setConfigJson("{\"streamSupported\":true}");

        when(providerConfigService.getOne(any())).thenReturn(providerConfig);

        String sse = "data: {\"choices\":[{\"delta\":{\"content\":\"hi\"}}]}\n\n" +
                "data: [DONE]\n\n";
        DataBuffer buffer = new DefaultDataBufferFactory().wrap(sse.getBytes(StandardCharsets.UTF_8));

        ExchangeFunction exchange = request -> {
            ClientResponse response = ClientResponse.create(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_EVENT_STREAM_VALUE)
                    .body(Flux.just(buffer))
                    .build();
            return reactor.core.publisher.Mono.just(response);
        };

        WebClient webClient = WebClient.builder().exchangeFunction(exchange).build();
        AiChatServiceImpl service = Mockito.spy(new AiChatServiceImpl(aiAppService, providerConfigService));
        doReturn(webClient).when(service).buildWebClient();

        AiChatRequest request = new AiChatRequest();
        request.setProviderCode("p3");
        request.setInput("hello");

        List<String> items = service.streamChat(request).collectList().block();

        assertThat(items).containsExactly(
                "{\"choices\":[{\"delta\":{\"content\":\"hi\"}}]}",
                "[DONE]"
        );
    }

    @Test
    void streamChat_deepseek_real() {
        AiProviderConfig providerConfig = providerConfigService.getById(1L);
        assertThat(providerConfig).isNotNull();
        assertThat(providerConfig.getCode()).isNotBlank();

        AiChatRequest request = new AiChatRequest();
        request.setProviderCode(providerConfig.getCode());
        request.setInput("hello");
        request.setModel("deepseek-chat");

        List<String> items = aiChatService.streamChat(request).collectList().block();

        System.out.printf(items.toString());
        assertThat(items).isNotEmpty();
    }
}
