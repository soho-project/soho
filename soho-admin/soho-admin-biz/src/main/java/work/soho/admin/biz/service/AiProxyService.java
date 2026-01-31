package work.soho.admin.biz.service;

import reactor.core.publisher.Flux;
import work.soho.admin.api.request.AiChatRequest;

public interface AiProxyService {
    Flux<String> streamChat(AiChatRequest req);
}
