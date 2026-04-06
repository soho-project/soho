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

    @Test
    public void balance_shouldDelegateToService() {
        AiOpenApiService aiOpenApiService = Mockito.mock(AiOpenApiService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService);
        Map<String, Object> expected = Map.of(
                "object", "balance",
                "is_active", true,
                "balance", 12.34,
                "unit", "USD"
        );
        when(aiOpenApiService.balance("Bearer token")).thenReturn(expected);

        Object result = controller.balance("Bearer token", "cc-switch/1.0");

        assertThat(result).isEqualTo(expected);
        verify(aiOpenApiService).balance("Bearer token");
    }

    @Test
    public void balance_whenServiceThrows_shouldReturnFallbackBalanceResponse() {
        AiOpenApiService aiOpenApiService = Mockito.mock(AiOpenApiService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService);
        when(aiOpenApiService.balance("Bearer token")).thenThrow(new IllegalArgumentException("bad token"));

        Object result = controller.balance("Bearer token", "cc-switch/1.0");
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;

        assertThat(result).isInstanceOf(Map.class);
        assertThat(resultMap).containsEntry("object", "balance");
        assertThat(resultMap).containsEntry("is_active", false);
        assertThat(resultMap).containsEntry("balance", 0);
        assertThat(resultMap).containsEntry("unit", "USD");
    }

    @Test
    public void self_shouldDelegateToService() {
        AiOpenApiService aiOpenApiService = Mockito.mock(AiOpenApiService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService);
        work.soho.common.security.userdetails.SohoUserDetails userDetails =
                new work.soho.common.security.userdetails.SohoUserDetails();
        userDetails.setId(123L);
        Map<String, Object> expected = Map.of(
                "success", true,
                "message", "success",
                "data", Map.of("group", "专业版套餐")
        );
        when(aiOpenApiService.selfPackage(123L, "123")).thenReturn(expected);

        Object result = controller.self(userDetails, "123");

        assertThat(result).isEqualTo(expected);
        verify(aiOpenApiService).selfPackage(123L, "123");
    }

    @Test
    public void self_whenServiceThrows_shouldReturnFallbackResponse() {
        AiOpenApiService aiOpenApiService = Mockito.mock(AiOpenApiService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService);
        work.soho.common.security.userdetails.SohoUserDetails userDetails =
                new work.soho.common.security.userdetails.SohoUserDetails();
        userDetails.setId(123L);
        when(aiOpenApiService.selfPackage(123L, "123")).thenThrow(new IllegalArgumentException("bad request"));

        Object result = controller.self(userDetails, "123");
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;

        assertThat(resultMap).containsEntry("success", false);
        assertThat(resultMap).containsEntry("message", "临时错误，如果长期错误请联系管理员");
    }
}
