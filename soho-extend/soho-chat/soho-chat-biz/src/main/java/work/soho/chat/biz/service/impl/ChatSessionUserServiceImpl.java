package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.enums.ChatSessionUserEnums;
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
        lambdaQueryWrapper.eq(ChatSessionUser::getStatus, ChatSessionUserEnums.Status.ACTIVE.getId());
        return list(lambdaQueryWrapper);
    }

    @Override
    public ChatSessionUser getSessionUser(Long sessionId, Long chatUserId) {
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getSessionId, sessionId);
        lambdaQueryWrapper.eq(ChatSessionUser::getUserId, chatUserId);
        return getOne(lambdaQueryWrapper);
    }

    @Override
    public List<ChatSessionUser> getSessionUserListByUid(Long uid) {
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getUserId, uid);
        return list(lambdaQueryWrapper);
    }
}
