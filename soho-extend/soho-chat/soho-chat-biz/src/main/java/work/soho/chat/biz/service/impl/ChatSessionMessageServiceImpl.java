package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatSessionMessage;
import work.soho.chat.biz.domain.ChatSessionMessageUser;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.enums.ChatSessionMessageUserEnums;
import work.soho.chat.biz.mapper.ChatSessionMessageMapper;
import work.soho.chat.biz.mapper.ChatSessionMessageUserMapper;
import work.soho.chat.biz.mapper.ChatSessionUserMapper;
import work.soho.chat.biz.service.ChatSessionMessageService;
import work.soho.chat.biz.service.ChatSessionService;
import work.soho.chat.biz.service.ChatSessionUserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatSessionMessageServiceImpl extends ServiceImpl<ChatSessionMessageMapper, ChatSessionMessage>
    implements ChatSessionMessageService{

    private final ChatSessionService chatSessionService;

    private final ChatSessionUserMapper chatSessionUserMapper;

    private final ChatSessionMessageUserMapper chatSessionMessageUserMapper;

    @Override
    public ChatSessionMessage dispatchingMessage(Long fromUid,Long sessionId,String content) {
        //插入消息
        ChatSessionMessage chatSessionMessage = new ChatSessionMessage();
        chatSessionMessage.setContent(content);
        chatSessionMessage.setSessionId(sessionId);
        chatSessionMessage.setFromUid(fromUid);
        chatSessionMessage.setUpdatedTime(LocalDateTime.now());
        chatSessionMessage.setCreatedTime(LocalDateTime.now());
        save(chatSessionMessage);

        //获取会话用户
        LambdaQueryWrapper<ChatSessionUser> chatSessionUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatSessionUserLambdaQueryWrapper.eq(ChatSessionUser::getSessionId, sessionId);
        List<ChatSessionUser> chatSessionUserList = chatSessionUserMapper.selectList(chatSessionUserLambdaQueryWrapper);

        //分发消息
        chatSessionUserList.forEach(sessionUser -> {
            ChatSessionMessageUser chatSessionMessageUser = new ChatSessionMessageUser();
            chatSessionMessageUser.setMessageId(chatSessionMessage.getId());
            chatSessionMessageUser.setUid(sessionUser.getUserId());
            chatSessionMessageUser.setIsRead(ChatSessionMessageUserEnums.IsRead.UNREAD.getId());
            chatSessionMessageUser.setUpdatedTime(LocalDateTime.now());
            chatSessionMessageUser.setCreatedTime(LocalDateTime.now());
            chatSessionMessageUser.setSessionId(sessionId);
            chatSessionMessageUserMapper.insert(chatSessionMessageUser);
        });

        return chatSessionMessage;
    }

    /**
     * 删除指定用户回话消息
     *
     * @param id
     * @param uid
     * @return
     */
    @Override
    public Boolean removeSessionMessageById(Long id, Long uid) {
        LambdaQueryWrapper<ChatSessionMessageUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionMessageUser::getSessionId, id)
                .eq(ChatSessionMessageUser::getUid, uid);
        chatSessionMessageUserMapper.delete(lambdaQueryWrapper);
        return true;
    }
}
