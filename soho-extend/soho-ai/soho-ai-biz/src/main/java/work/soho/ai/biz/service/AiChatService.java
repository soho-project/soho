package work.soho.ai.biz.service;

import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.request.AiChatRequest;
import reactor.core.publisher.Flux;

public interface AiChatService {
    AiChatResponse chat(AiChatRequest request);
    Flux<String> streamChat(AiChatRequest request);
}
