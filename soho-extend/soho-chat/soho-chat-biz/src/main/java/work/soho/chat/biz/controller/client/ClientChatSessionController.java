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
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.service.ChatSessionService;
import work.soho.chat.biz.service.ChatSessionUserService;
import work.soho.chat.biz.vo.UserSessionVO;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户端会话列表控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/chat/chat-session")
public class ClientChatSessionController {
    private final ChatSessionService chatSessionService;

    private final ChatSessionUserService chatSessionUserService;

    /**
     * 查询聊天会话列表
     */
    @GetMapping("/list")
    public R<PageSerializable<UserSessionVO>> list(ChatSession chatSession, BetweenCreatedTimeRequest betweenCreatedTimeRequest
        ,@AuthenticationPrincipal SohoUserDetails sohoUserDetails
    )
    {
        //查找用户对应的会话
        LambdaQueryWrapper<ChatSessionUser> chatSessionUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatSessionUserLambdaQueryWrapper.eq(ChatSessionUser::getUserId, sohoUserDetails.getId());
        List<ChatSessionUser> chatSessionUserList = chatSessionUserService.list(chatSessionUserLambdaQueryWrapper);
        if(chatSessionUserList == null || chatSessionUserList.size() == 0) {
            return R.success();
        }
        List<Long> sessionIdList = chatSessionUserList.stream().map(ChatSessionUser::getSessionId).collect(Collectors.toList());

        PageUtils.startPage();
        LambdaQueryWrapper<ChatSession> lqw = new LambdaQueryWrapper<ChatSession>();
        lqw.in(ChatSession::getId, sessionIdList);
        lqw.eq(chatSession.getId() != null, ChatSession::getId ,chatSession.getId());
        lqw.eq(chatSession.getType() != null, ChatSession::getType ,chatSession.getType());
        lqw.eq(chatSession.getStatus() != null, ChatSession::getStatus ,chatSession.getStatus());
        lqw.eq(chatSession.getTitle() != null, ChatSession::getTitle ,chatSession.getTitle());
        lqw.eq(chatSession.getAvatar() != null, ChatSession::getAvatar ,chatSession.getAvatar());
        lqw.eq(chatSession.getUpdatedTime() != null, ChatSession::getUpdatedTime ,chatSession.getUpdatedTime());
        lqw.eq(chatSession.getCreatedTime() != null, ChatSession::getCreatedTime ,chatSession.getCreatedTime());
        lqw.gt(betweenCreatedTimeRequest.getStartTime()!= null, ChatSession::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.le(betweenCreatedTimeRequest.getEndTime()!= null, ChatSession::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ChatSession::getUpdatedTime);
        List<ChatSession> list = chatSessionService.list(lqw);

        Map<Long, ChatSessionUser> sessionUserMap = chatSessionUserList.stream().collect(Collectors.toMap(ChatSessionUser::getSessionId, item -> item));
        List<UserSessionVO> list1 = new ArrayList<>();
        list.forEach(session -> {
            UserSessionVO userSessionVO = BeanUtils.copy(session, UserSessionVO.class);
            userSessionVO.setLastLookMessageTime(sessionUserMap.get(session.getId()).getLastLookMessageTime());
            list1.add(userSessionVO);
        });
        PageSerializable<UserSessionVO> pageSerializable = new PageSerializable<>();
        pageSerializable.setTotal(list.size());
        pageSerializable.setList(list1);
        return R.success(pageSerializable);
    }

    @GetMapping(value = "/{id}" )
    public R<ChatSession> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatSessionService.getById(id));
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

    /**
     * 更新用户会话最后查看时间
     *
     * @param sessionId
     * @param lastLookMessageTime
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/updateLastLookTime")
    public R<Boolean> updateLastLookTime(Long sessionId, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getSessionId, sessionId)
                .eq(ChatSessionUser::getUserId, sohoUserDetails.getId());
        ChatSessionUser chatSessionUser = chatSessionUserService.getOne(lambdaQueryWrapper);
        chatSessionUser.setLastLookMessageTime(LocalDateTime.now());
        chatSessionUser.setUpdatedTime(LocalDateTime.now());
        chatSessionUserService.updateById(chatSessionUser);
        return R.success(true);
    }
}
