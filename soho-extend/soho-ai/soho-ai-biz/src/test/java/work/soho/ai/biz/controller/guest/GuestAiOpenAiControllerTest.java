package work.soho.ai.biz.controller.guest;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.request.OpenAiChatCompletionRequest;
import work.soho.ai.biz.request.OpenAiResponsesRequest;
import work.soho.ai.biz.service.AiOpenApiService;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GuestAiOpenAiControllerTest {

    @Test
    public void buildLogSummary_shouldNotContainSensitivePayload() {
        AiOpenApiService aiOpenApiService = Mockito.mock(AiOpenApiService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService);

        OpenAiChatCompletionRequest chatRequest = new OpenAiChatCompletionRequest();
        chatRequest.setModel("gpt-4o-mini");
        chatRequest.setStream(true);
        OpenAiChatCompletionRequest.Message message = new OpenAiChatCompletionRequest.Message();
        message.setRole("user");
        message.setContent("very sensitive prompt");
        chatRequest.setMessages(List.of(message));

        Map<String, Object> chatSummary = controller.buildChatCompletionsLogSummary(chatRequest);
        assertThat(chatSummary).containsEntry("model", "gpt-4o-mini");
        assertThat(chatSummary).containsEntry("stream", true);
        assertThat(chatSummary).containsEntry("messageCount", 1);
        assertThat(chatSummary).doesNotContainKeys("messages", "content", "prompt", "input");

        OpenAiResponsesRequest responsesRequest = new OpenAiResponsesRequest();
        responsesRequest.setModel("gpt-5.4");
        responsesRequest.setStream(true);
        responsesRequest.setInput("secret input");
        responsesRequest.setTools(List.of(Map.of("name", "tool1")));
        responsesRequest.setInclude(List.of("usage"));

        Map<String, Object> responsesSummary = controller.buildResponsesLogSummary(responsesRequest);
        assertThat(responsesSummary).containsEntry("model", "gpt-5.4");
        assertThat(responsesSummary).containsEntry("stream", true);
        assertThat(responsesSummary).containsEntry("toolsCount", 1);
        assertThat(responsesSummary).containsEntry("includeCount", 1);
        assertThat(responsesSummary).containsEntry("hasInput", true);
        assertThat(responsesSummary).doesNotContainKeys("input", "tools", "messages", "prompt");
    }

    @Test
    public void dispose_whenDisposableActive_shouldDispose() throws Exception {
        AiOpenApiService aiOpenApiService = Mockito.mock(AiOpenApiService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService);

        Method method = GuestAiOpenAiController.class
                .getDeclaredMethod("dispose", AtomicReference.class);
        method.setAccessible(true);

        Disposable disposable = Mockito.mock(Disposable.class);
        when(disposable.isDisposed()).thenReturn(false);
        AtomicReference<Disposable> disposableRef = new AtomicReference<>(disposable);

        method.invoke(controller, disposableRef);
        verify(disposable).dispose();
        assertThat(disposableRef.get()).isNull();
    }
}
