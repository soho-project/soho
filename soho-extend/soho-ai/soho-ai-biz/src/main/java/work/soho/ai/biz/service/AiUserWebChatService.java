package work.soho.ai.biz.service;

import reactor.core.publisher.Flux;
import work.soho.ai.biz.domain.AiChatSession;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.dto.AiUserModelView;
import work.soho.ai.biz.request.UserAiChatRequest;

import java.util.List;

public interface AiUserWebChatService {
    List<AiUserModelView> listModels();

    List<AiChatSession> listSessions(Long userId);

    List<work.soho.ai.biz.domain.AiChatSessionMessage> listSessionMessages(Long userId, Long sessionId);

    boolean deleteSession(Long userId, Long sessionId);

    AiChatSession renameSession(Long userId, Long sessionId, String title);

    AiChatResponse chat(Long userId, UserAiChatRequest request);

    Flux<String> streamChat(Long userId, UserAiChatRequest request);
}
