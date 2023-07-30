package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatSessionMessageUser;
import work.soho.chat.biz.enums.ChatSessionMessageUserEnums;
import work.soho.chat.biz.mapper.ChatSessionMessageUserMapper;
import work.soho.chat.biz.service.ChatSessionMessageUserService;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ChatSessionMessageUserServiceImpl extends ServiceImpl<ChatSessionMessageUserMapper, ChatSessionMessageUser>
    implements ChatSessionMessageUserService{

    @Override
    public void isRead(Long messageId, Long uid) {
        LambdaQueryWrapper<ChatSessionMessageUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionMessageUser::getMessageId, messageId);
        lambdaQueryWrapper.eq(ChatSessionMessageUser::getUid, uid);
        ChatSessionMessageUser chatSessionMessageUser = getOne(lambdaQueryWrapper);
        chatSessionMessageUser.setIsRead(ChatSessionMessageUserEnums.IsRead.READ.getId());
        chatSessionMessageUser.setUpdatedTime(LocalDateTime.now());
        updateById(chatSessionMessageUser);
    }
}
