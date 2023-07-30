package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.mapper.ChatSessionUserMapper;
import work.soho.chat.biz.service.ChatSessionUserService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatSessionUserServiceImpl extends ServiceImpl<ChatSessionUserMapper, ChatSessionUser>
    implements ChatSessionUserService{

    @Override
    public List<ChatSessionUser> getSessionUserList(Long sessionId) {
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getSessionId, sessionId);
        return list(lambdaQueryWrapper);
    }
}
