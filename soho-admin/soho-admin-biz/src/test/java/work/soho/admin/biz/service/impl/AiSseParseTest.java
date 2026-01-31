package work.soho.admin.biz.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import work.soho.admin.biz.config.DeepSeekProperties;
import work.soho.admin.biz.config.GeminiProperties;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiSseParseTest {

    @Test
    void deepSeek_parses_chunked_sse() throws Exception {
        DeepSeekProxyServiceImpl service = new DeepSeekProxyServiceImpl(
                WebClient.builder().build(),
                new DeepSeekProperties()
        );

        Flux<String> raw = Flux.just(
                "data: {\"id\":",
                "1}\n",
                "\n",
                "data: {\"id\":2}\r\n\r\n",
                ": keep-alive\r\n\r\n",
                "data: [DONE]\n\n"
        );

        List<String> out = invokeSseToPayload(service, raw).collectList().block();
        assertEquals(List.of("{\"id\":1}", "{\"id\":2}", "[DONE]"), out);
    }

    @Test
    void gemini_parses_multi_data_lines() throws Exception {
        GeminiSeekProxyServiceImpl service = new GeminiSeekProxyServiceImpl(
                WebClient.builder().build(),
                new GeminiProperties()
        );

        Flux<String> raw = Flux.just(
                "data: {\"a\":1}\n",
                "data: {\"b\":2}\n\n"
        );

        List<String> out = invokeSseToPayload(service, raw).collectList().block();
        assertEquals(List.of("{\"a\":1}", "{\"b\":2}"), out);
    }

    @SuppressWarnings("unchecked")
    private static Flux<String> invokeSseToPayload(Object service, Flux<String> rawTextFlux) throws Exception {
        Method method = service.getClass().getDeclaredMethod("sseToPayloadFlux", Flux.class);
        method.setAccessible(true);
        return (Flux<String>) method.invoke(service, rawTextFlux);
    }
}
