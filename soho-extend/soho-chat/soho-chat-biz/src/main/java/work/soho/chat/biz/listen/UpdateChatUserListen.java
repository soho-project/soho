package work.soho.chat.biz.listen;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import work.soho.chat.biz.domain.ChatSession;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.enums.ChatSessionEnums;
import work.soho.chat.biz.service.ChatSessionService;
import work.soho.chat.biz.service.ChatSessionUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class UpdateChatUserListen {
    private final ChatSessionService chatSessionService;
    private final ChatSessionUserService chatSessionUserService;

    /**
     * 用户信息变更监听
     *
     * 1 好友会话变更Title
     *
     * @param chatUser
     */
    @EventListener
    public void changeUser(ChatUser chatUser) {
        List<ChatSessionUser> chatSessionUserList = chatSessionUserService.getSessionUserListByUid(chatUser.getId());
        List<Long> sessionIds = chatSessionUserList.stream().map(ChatSessionUser::getSessionId).collect(Collectors.toList());
        List<ChatSession> chatSessionList = chatSessionService.getBaseMapper().selectBatchIds(sessionIds);
        //获取好友会话
        List<ChatSession> friendSessionList = chatSessionList.stream().filter(item->item.getType() == ChatSessionEnums.Type.PRIVATE_CHAT.getId()).collect(Collectors.toList());

        List<Long> friendSessionIds = friendSessionList.stream().map(ChatSession::getId).collect(Collectors.toList());

        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ChatSessionUser::getSessionId, friendSessionIds);
        lambdaQueryWrapper.ne(ChatSessionUser::getUserId, chatUser.getId());
        List<ChatSessionUser> updateChatSessionUserList = chatSessionUserService.list(lambdaQueryWrapper);

        String title = chatUser.getNickname() != null && !chatUser.equals("") ? chatUser.getNickname() : chatUser.getUsername();
        updateChatSessionUserList.forEach(item->{
            item.setOriginTitle(title);
            item.setAvatar(chatUser.getAvatar());
        });

        chatSessionUserService.saveOrUpdateBatch(updateChatSessionUserList);

        //TODO 发送消息给客户端进行会话更新
    }
}
