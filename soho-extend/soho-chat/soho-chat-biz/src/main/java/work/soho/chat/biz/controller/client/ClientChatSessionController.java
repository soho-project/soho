package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.api.admin.annotation.Node;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.chat.biz.domain.ChatSession;
import work.soho.chat.biz.domain.ChatSessionMessage;
import work.soho.chat.biz.domain.ChatSessionMessageUser;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.service.ChatSessionMessageService;
import work.soho.chat.biz.service.ChatSessionMessageUserService;
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

    private final ChatSessionMessageService chatSessionMessageService;

    private final ChatSessionMessageUserService chatSessionMessageUserService;

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
     * 好友聊天获取会话信息
     *
     * 好友聊天会话创建或者获取
     *
     * @param friendUid
     * @return
     */
    @GetMapping(value = "/friendSessionInfo")
    public R<ChatSession> getOrCreate(Long friendUid, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        return R.success(chatSessionService.findFriendSession(sohoUserDetails.getId(), friendUid.longValue()));
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
            LambdaQueryWrapper<ChatSessionUser> chatSessionUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            chatSessionUserLambdaQueryWrapper.eq(ChatSessionUser::getUserId, sohoUserDetails)
                            .eq(ChatSessionUser::getSessionId, chatSession.getId());
            //TODO 会话类型判断
            ChatSessionUser chatSessionUser = chatSessionUserService.getOne(chatSessionUserLambdaQueryWrapper);
            Assert.notNull(chatSessionUser, "会话不存在");

            //删除会话用户
            LambdaQueryWrapper<ChatSessionUser> chatSessionUserLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            chatSessionUserLambdaQueryWrapper1.eq(ChatSessionUser::getSessionId, chatSession.getId());
            chatSessionUserService.remove(chatSessionUserLambdaQueryWrapper1);

            chatSessionService.removeById(chatSession.getId());
        }
        return R.success(true);
    }

    /**
     * 更新用户会话最后查看时间
     *
     * @param sessionId
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

    /**
     * 获取会话历史消息
     *
     * @param sessionId
     * @param lastMessageId  最后一个ID
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/history-message")
    public R<List<ChatSessionMessage>> historyMessage(Long sessionId, Long lastMessageId, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        try {
            ChatSession chatSession = chatSessionService.getById(sessionId);
            Assert.notNull(chatSession, "会话不存在");
            LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ChatSessionUser::getUserId, sohoUserDetails.getId());
            lambdaQueryWrapper.eq(ChatSessionUser::getSessionId, sessionId);
            ChatSessionUser chatSessionUser = chatSessionUserService.getOne(lambdaQueryWrapper);
            Assert.notNull(chatSessionUser, "会话不存在");

            //读取用户历史消息
            LambdaQueryWrapper<ChatSessionMessage> sessionMessageLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sessionMessageLambdaQueryWrapper.eq(ChatSessionMessage::getSessionId, sessionId);
            if(lastMessageId!=null) {
                sessionMessageLambdaQueryWrapper.lt(ChatSessionMessage::getId, lastMessageId);
            }
            sessionMessageLambdaQueryWrapper.orderByDesc(ChatSessionMessage::getId);
            sessionMessageLambdaQueryWrapper.last("limit 100");
            List<ChatSessionMessage> list = chatSessionMessageService.list(sessionMessageLambdaQueryWrapper);
            //对结果进行顺序排序
            list.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
            return R.success(list);
        } catch (Exception e) {
            return R.success(new ArrayList<>());
        }
    }

    /**
     * 删除用户会话消息
     *
     * @param sohoUserDetails
     * @return
     */
    @DeleteMapping("/delete-user-message/{id}")
    public R<Boolean> deleteUserMessage(@PathVariable Long id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        chatSessionMessageService.removeSessionMessageById(id, sohoUserDetails.getId());
        return R.success(true);
    }

    /**
     * 编辑用户会话信息
     *
     * @param chatSessionUser
     * @return
     */
    @PutMapping("/updateUser")
    public R<Boolean> updateUser(ChatSessionUser chatSessionUser, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getUserId, sohoUserDetails.getId())
                .eq(ChatSessionUser::getSessionId, chatSessionUser.getSessionId());
        ChatSessionUser oldChatSessionUser = chatSessionUserService.getOne(lambdaQueryWrapper);
        Assert.notNull(oldChatSessionUser, "无权访问");
        chatSessionUser.setId(oldChatSessionUser.getId());
        return R.success(chatSessionUserService.updateById(chatSessionUser));
    }

    /**
     * 离开会话
     *
     * @param sessionId
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/leave")
    public R<Boolean> leave(Long sessionId, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getSessionId, sessionId)
                .eq(ChatSessionUser::getUserId, sohoUserDetails.getId());
        return R.success(chatSessionUserService.remove(lambdaQueryWrapper));
    }

}
