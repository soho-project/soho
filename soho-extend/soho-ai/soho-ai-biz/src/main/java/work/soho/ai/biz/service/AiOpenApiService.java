package work.soho.ai.biz.service;

import reactor.core.publisher.Flux;
import work.soho.ai.biz.request.OpenAiChatCompletionRequest;
import work.soho.ai.biz.request.OpenAiResponsesRequest;

import java.util.Map;

public interface AiOpenApiService {
    Map<String, Object> models(String authorization);

    Map<String, Object> chatCompletions(String authorization, OpenAiChatCompletionRequest request);

    Flux<String> streamChatCompletions(String authorization, OpenAiChatCompletionRequest request);

    Map<String, Object> responses(String authorization, OpenAiResponsesRequest request);

    Flux<String> streamResponses(String authorization, OpenAiResponsesRequest request);
}
