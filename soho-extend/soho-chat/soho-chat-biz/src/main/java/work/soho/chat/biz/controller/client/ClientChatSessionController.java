package work.soho.chat.biz.controller.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.api.admin.annotation.Node;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.chat.biz.domain.ChatSession;
import work.soho.chat.biz.service.ChatSessionService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;

import java.util.List;

/**
 * 客户端会话列表控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/chat/chat-session")
public class ClientChatSessionController {
    private final ChatSessionService chatSessionService;

    /**
     * 查询聊天会话列表
     */
    @GetMapping("/list")
    @Node(value = "chatSession::list", name = "聊天会话列表")
    public R<PageSerializable<ChatSession>> list(ChatSession chatSession, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatSession> lqw = new LambdaQueryWrapper<ChatSession>();
        lqw.eq(chatSession.getId() != null, ChatSession::getId ,chatSession.getId());
        lqw.eq(chatSession.getType() != null, ChatSession::getType ,chatSession.getType());
        lqw.eq(chatSession.getStatus() != null, ChatSession::getStatus ,chatSession.getStatus());
        lqw.eq(chatSession.getTitle() != null, ChatSession::getTitle ,chatSession.getTitle());
        lqw.eq(chatSession.getAvatar() != null, ChatSession::getAvatar ,chatSession.getAvatar());
        lqw.eq(chatSession.getUpdatedTime() != null, ChatSession::getUpdatedTime ,chatSession.getUpdatedTime());
        lqw.eq(chatSession.getCreatedTime() != null, ChatSession::getCreatedTime ,chatSession.getCreatedTime());
        lqw.gt(betweenCreatedTimeRequest.getStartTime()!= null, ChatSession::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.le(betweenCreatedTimeRequest.getEndTime()!= null, ChatSession::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ChatSession::getId);
        List<ChatSession> list = chatSessionService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 删除指定会话
     *
     * @param chatSession
     * @return
     */
    @DeleteMapping
    public R<Boolean> delete(@RequestBody ChatSession chatSession, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        chatSession = chatSessionService.getById(chatSession.getId());
        if(chatSession != null) {
            //验证会话是否属于当前用户

            chatSessionService.removeById(chatSession.getId());
        }
        return R.success(true);
    }
}
