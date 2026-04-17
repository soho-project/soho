package work.soho.ai.biz.controller.guest;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import work.soho.ai.biz.dto.AiOpenApiGuardContext;
import work.soho.ai.biz.exception.AiOpenApiGuardException;
import work.soho.ai.biz.request.OpenAiChatCompletionRequest;
import work.soho.ai.biz.request.OpenAiResponsesRequest;
import work.soho.ai.biz.service.AiOpenApiGuardService;
import work.soho.ai.biz.service.AiOpenApiService;
import work.soho.common.security.userdetails.SohoUserDetails;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GuestAiOpenAiControllerTest {

    @Test
    public void buildLogSummary_shouldNotContainSensitivePayload() {
        AiOpenApiService aiOpenApiService = Mockito.mock(AiOpenApiService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService, Mockito.mock(AiOpenApiGuardService.class));

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
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService, Mockito.mock(AiOpenApiGuardService.class));

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
        AiOpenApiGuardService guardService = Mockito.mock(AiOpenApiGuardService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService, guardService);
        Map<String, Object> expected = Map.of(
                "object", "balance",
                "is_active", true,
                "balance", 12.34,
                "unit", "USD"
        );
        when(guardService.checkAndAcquire("Bearer token", "/ai/guest/openai/v1/user/balance"))
                .thenReturn(new AiOpenApiGuardContext());
        when(aiOpenApiService.balance("Bearer token")).thenReturn(expected);

        Object result = controller.balance("Bearer token", "cc-switch/1.0");

        assertThat(result).isEqualTo(expected);
        verify(aiOpenApiService).balance("Bearer token");
    }

    @Test
    public void balance_whenGuardRejects_shouldReturnFallbackBalanceResponse() {
        AiOpenApiService aiOpenApiService = Mockito.mock(AiOpenApiService.class);
        AiOpenApiGuardService guardService = Mockito.mock(AiOpenApiGuardService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService, guardService);
        when(guardService.checkAndAcquire("Bearer token", "/ai/guest/openai/v1/user/balance"))
                .thenThrow(new AiOpenApiGuardException("rate limit", "请求过于频繁，请稍后再试", "rate_limit_exceeded",
                        "rate_limit", true, false, 429));

        Object result = controller.balance("Bearer token", "cc-switch/1.0");
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;

        assertThat(resultMap).containsEntry("object", "balance");
        assertThat(resultMap).containsEntry("is_active", false);
        assertThat(resultMap).containsEntry("balance", 0);
        assertThat(resultMap).containsEntry("unit", "USD");
        verify(aiOpenApiService, never()).balance(any());
    }

    @Test
    public void self_shouldDelegateToService() {
        AiOpenApiService aiOpenApiService = Mockito.mock(AiOpenApiService.class);
        AiOpenApiGuardService guardService = Mockito.mock(AiOpenApiGuardService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService, guardService);
        SohoUserDetails userDetails = new SohoUserDetails();
        userDetails.setId(123L);
        Map<String, Object> expected = Map.of(
                "success", true,
                "message", "success",
                "data", Map.of("group", "专业版套餐")
        );
        when(guardService.checkAndAcquire("Bearer token", "/ai/guest/openai/v1/api/user/self"))
                .thenReturn(new AiOpenApiGuardContext());
        when(aiOpenApiService.selfPackage(123L, "123")).thenReturn(expected);

        Object result = controller.self(userDetails, "123", "Bearer token");

        assertThat(result).isEqualTo(expected);
        verify(aiOpenApiService).selfPackage(123L, "123");
    }

    @Test
    public void self_whenServiceThrows_shouldReturnFallbackResponse() {
        AiOpenApiService aiOpenApiService = Mockito.mock(AiOpenApiService.class);
        AiOpenApiGuardService guardService = Mockito.mock(AiOpenApiGuardService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService, guardService);
        SohoUserDetails userDetails = new SohoUserDetails();
        userDetails.setId(123L);
        AiOpenApiGuardContext guardContext = new AiOpenApiGuardContext();
        when(guardService.checkAndAcquire("Bearer token", "/ai/guest/openai/v1/api/user/self"))
                .thenReturn(guardContext);
        when(aiOpenApiService.selfPackage(123L, "123")).thenThrow(new IllegalArgumentException("bad request"));

        Object result = controller.self(userDetails, "123", "Bearer token");
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;

        assertThat(resultMap).containsEntry("success", false);
        assertThat(resultMap).containsEntry("message", "bad request");
        verify(guardService).recordFailure(eq(guardContext), any(IllegalArgumentException.class));
    }

    @Test
    public void models_whenGuardRejects_shouldReturnOpenAiErrorShape() {
        AiOpenApiService aiOpenApiService = Mockito.mock(AiOpenApiService.class);
        AiOpenApiGuardService guardService = Mockito.mock(AiOpenApiGuardService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService, guardService);
        when(guardService.checkAndAcquire("Bearer token", "/ai/guest/openai/v1/models"))
                .thenThrow(new AiOpenApiGuardException("temporary ban", "api key已被临时封禁", "temporarily_banned",
                        "temporary_ban", true, true, 403));

        Object result = controller.models("Bearer token");
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        @SuppressWarnings("unchecked")
        Map<String, Object> error = (Map<String, Object>) resultMap.get("error");

        assertThat(error).containsEntry("message", "api key已被临时封禁");
        assertThat(error).containsEntry("type", "request_error");
        assertThat(error).containsEntry("code", "temporarily_banned");
        verify(aiOpenApiService, never()).models(any());
    }

    @Test
    public void models_whenServiceThrowsUnexpectedException_shouldReturnGenericErrorAndRecordFailure() {
        AiOpenApiService aiOpenApiService = Mockito.mock(AiOpenApiService.class);
        AiOpenApiGuardService guardService = Mockito.mock(AiOpenApiGuardService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService, guardService);
        AiOpenApiGuardContext guardContext = new AiOpenApiGuardContext();
        when(guardService.checkAndAcquire("Bearer token", "/ai/guest/openai/v1/models"))
                .thenReturn(guardContext);
        when(aiOpenApiService.models("Bearer token"))
                .thenThrow(new RuntimeException("upstream 502 https://example.com/internal"));

        Object result = controller.models("Bearer token");
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        @SuppressWarnings("unchecked")
        Map<String, Object> error = (Map<String, Object>) resultMap.get("error");

        assertThat(error).containsEntry("message", "临时错误，如果长期错误请联系管理员");
        assertThat(error).containsEntry("type", "server_error");
        assertThat(error).containsEntry("code", "server_error");
        verify(guardService).recordFailure(eq(guardContext), any(RuntimeException.class));
    }

    @Test
    public void chatCompletions_stream_shouldReturnEmitterWhenGuardPasses() {
        AiOpenApiService aiOpenApiService = Mockito.mock(AiOpenApiService.class);
        AiOpenApiGuardService guardService = Mockito.mock(AiOpenApiGuardService.class);
        GuestAiOpenAiController controller = new GuestAiOpenAiController(aiOpenApiService, guardService);
        OpenAiChatCompletionRequest request = new OpenAiChatCompletionRequest();
        request.setStream(true);
        when(guardService.checkAndAcquire("Bearer token", "/ai/guest/openai/v1/chat/completions"))
                .thenReturn(new AiOpenApiGuardContext());
        when(aiOpenApiService.streamChatCompletions("Bearer token", request)).thenReturn(Flux.empty());

        Object result = controller.chatCompletions("Bearer token", request);

        assertThat(result).isInstanceOf(SseEmitter.class);
    }
}
