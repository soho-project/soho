package work.soho.ai.biz.service;

import reactor.core.publisher.Flux;
import work.soho.ai.biz.request.OpenAiChatCompletionRequest;

import java.util.Map;

public interface AiOpenApiService {
    Map<String, Object> chatCompletions(String authorization, OpenAiChatCompletionRequest request);

    Flux<String> streamChatCompletions(String authorization, OpenAiChatCompletionRequest request);
}
