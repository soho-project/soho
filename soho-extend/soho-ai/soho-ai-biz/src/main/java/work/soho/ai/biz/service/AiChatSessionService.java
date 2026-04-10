package work.soho.ai.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.ai.biz.domain.AiChatSession;

public interface AiChatSessionService extends IService<AiChatSession> {
    /**
     * 按会话归属方校验并获取会话。
     *
     * @param ownerId 归属方ID
     * @param sessionId 会话ID
     * @return 会话
     */
    AiChatSession requireSessionByOwnerId(Long ownerId, Long sessionId);

    /**
     * 按普通用户归属校验并获取会话。
     *
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 会话
     */
    AiChatSession requireOwnedSession(Long userId, Long sessionId);
}
