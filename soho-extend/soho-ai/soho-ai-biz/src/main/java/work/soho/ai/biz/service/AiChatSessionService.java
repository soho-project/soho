package work.soho.ai.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.ai.biz.domain.AiChatSession;

public interface AiChatSessionService extends IService<AiChatSession> {
    AiChatSession requireOwnedSession(Long userId, Long sessionId);
}
