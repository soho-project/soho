package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.soho.ai.biz.domain.AiChatSession;
import work.soho.ai.biz.mapper.AiChatSessionMapper;
import work.soho.ai.biz.service.AiChatSessionService;

@Service
public class AiChatSessionServiceImpl extends ServiceImpl<AiChatSessionMapper, AiChatSession>
        implements AiChatSessionService {
    @Override
    public AiChatSession requireSessionByOwnerId(Long ownerId, Long sessionId) {
        AiChatSession session = getOne(new LambdaQueryWrapper<AiChatSession>()
                .eq(AiChatSession::getId, sessionId)
                .eq(AiChatSession::getUserId, ownerId)
                .last("limit 1"));
        Assert.notNull(session, "session不存在");
        return session;
    }

    @Override
    public AiChatSession requireOwnedSession(Long userId, Long sessionId) {
        return requireSessionByOwnerId(userId, sessionId);
    }
}
