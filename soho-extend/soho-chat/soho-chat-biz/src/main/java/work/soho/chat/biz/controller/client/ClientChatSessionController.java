package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.hash.Hash;
import com.aliyun.oss.model.UploadFileResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.api.admin.service.AdminConfigApiService;
import work.soho.chat.api.ChatMessage;
import work.soho.chat.api.payload.Command;
import work.soho.chat.api.payload.RealTimeCmd;
import work.soho.chat.api.payload.SystemMessage;
import work.soho.chat.biz.domain.*;
import work.soho.chat.biz.enums.ChatSessionEnums;
import work.soho.chat.biz.enums.ChatSessionUserEnums;
import work.soho.chat.biz.req.BatchUpdateNotificationReq;
import work.soho.chat.biz.service.*;
import work.soho.chat.biz.vo.SessionUserVO;
import work.soho.chat.biz.vo.UserSessionVO;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.upload.utils.UploadUtils;
import work.soho.upload.api.Upload;
import work.soho.upload.api.vo.UploadInfoVo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户端会话列表控制器
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/chat/chat-session")
public class ClientChatSessionController {
    private final AdminConfigApiService adminConfigApiService;
    private final ChatSessionService chatSessionService;

    private final ChatSessionUserService chatSessionUserService;

    private final ChatGroupService chatGroupService;
    private final ChatGroupUserService chatGroupUserService;

    private final ChatSessionMessageService chatSessionMessageService;

    private final ChatService chatService;

    private final ChatSessionMessageUserService chatSessionMessageUserService;

    private final ChatUserFriendService chatUserFriendService;

    private final ChatUserService chatUserService;

    private final Upload upload;

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
        chatSessionUserLambdaQueryWrapper.eq(ChatSessionUser::getStatus, ChatSessionUserEnums.Status.ACTIVE.getId());
        List<ChatSessionUser> chatSessionUserList = chatSessionUserService.list(chatSessionUserLambdaQueryWrapper);
        if(chatSessionUserList == null || chatSessionUserList.size() == 0) {
            PageSerializable<UserSessionVO> page = new PageSerializable<>();
            page.setTotal(0);
            page.setList(new ArrayList<>());
            return R.success(page);
        }
        List<Long> sessionIdList = chatSessionUserList.stream().map(ChatSessionUser::getSessionId).collect(Collectors.toList());
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
        lqw.orderByDesc(ChatSession::getLastMessageTime);
        List<ChatSession> list = chatSessionService.list(lqw);

        Map<Long, ChatSessionUser> sessionUserMap = chatSessionUserList.stream().collect(Collectors.toMap(ChatSessionUser::getSessionId, item -> item));
        List<UserSessionVO> list1 = new ArrayList<>();
        list.forEach(session -> {
            UserSessionVO userSessionVO = BeanUtils.copy(session, UserSessionVO.class);
            userSessionVO.setLastLookMessageTime(sessionUserMap.get(session.getId()).getLastLookMessageTime());
            userSessionVO.setAliasTitle(sessionUserMap.get(session.getId()).getTitle());
            //设置头像
            ChatSessionUser chatSessionUser = sessionUserMap.get(session.getId());
            if(StringUtils.isNotEmpty(chatSessionUser.getAvatar())) {
                userSessionVO.setAvatar(chatSessionUser.getAvatar());
            }
            //原始标题
            if(StringUtils.isNotEmpty(chatSessionUser.getOriginTitle())) {
                userSessionVO.setTitle(chatSessionUser.getOriginTitle());
            }
            //用户备注名
            if(StringUtils.isNotEmpty(chatSessionUser.getTitle())) {
                userSessionVO.setTitle(chatSessionUser.getTitle());
            }
            userSessionVO.setUnreadCount(chatSessionUser.getUnreadCount());
            userSessionVO.setIsNotDisturb(chatSessionUser.getIsNotDisturb());
            userSessionVO.setIsShield(chatSessionUser.getIsShield());
            userSessionVO.setIsTop(chatSessionUser.getIsTop());
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
        //TODO 检查好友关系
        return R.success(chatSessionService.findFriendSession(sohoUserDetails.getId(), friendUid.longValue()));
    }

    @GetMapping("/groupSessionInfo")
    public R<ChatSession> getOrCreateGroupSession(Long groupId, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        //检查当前用户是否在群中
        ChatGroup chatGroup = chatGroupService.getById(groupId);
        Assert.notNull(chatGroup);
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getGroupId, groupId)
                .eq(ChatGroupUser::getChatUid, sohoUserDetails.getId());
        ChatGroupUser chatGroupUser = chatGroupUserService.getOne(lambdaQueryWrapper);
        Assert.notNull(chatGroupUser);
        //获取群聊列表
        LambdaQueryWrapper<ChatGroupUser> chatGroupUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatGroupUserLambdaQueryWrapper.eq(ChatGroupUser::getGroupId, chatGroup.getId());
        List<ChatGroupUser> list = chatGroupUserService.list(chatGroupUserLambdaQueryWrapper);
        return R.success(chatSessionService.groupSession(chatGroup, list));
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
        chatSessionUser.setUnreadCount(0); //重置未读消息数量
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
            sessionMessageLambdaQueryWrapper.eq(ChatSessionMessage::getIsDeleted, 0);
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
    public R<Boolean> updateUser(@RequestBody ChatSessionUser chatSessionUser, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getUserId, sohoUserDetails.getId())
                .eq(ChatSessionUser::getSessionId, chatSessionUser.getSessionId());
        ChatSessionUser oldChatSessionUser = chatSessionUserService.getOne(lambdaQueryWrapper);
        Assert.notNull(oldChatSessionUser, "无权访问");
        chatSessionUser.setId(oldChatSessionUser.getId());
        return R.success(chatSessionUserService.updateById(chatSessionUser));
    }

    @PutMapping()
    public R<Boolean> update(@RequestBody ChatSession chatSession, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatSession originChatSession = chatSessionService.getById(chatSession.getId());
        Assert.notNull(originChatSession, "会话不存在");
        List<ChatSessionUser> chatSessionUserList = chatSessionUserService.getSessionUserList(chatSession.getId());
        //断言只有群组才能进行标题头像编辑
        Assert.equals(originChatSession.getType(), ChatSessionEnums.Type.GROUP_CHAT.getId(), "只有群组才能调用该接口");
        Assert.isTrue(chatGroupService.isAdmin(originChatSession.getTrackId(), sohoUserDetails.getId()), "非管理员无权修改");

        originChatSession.setTitle(chatSession.getTitle());
        originChatSession.setAvatar(chatSession.getAvatar());
        originChatSession.setUpdatedTime(LocalDateTime.now());
        chatSessionService.updateById(originChatSession);

        //同步到群组
        ChatGroup chatGroup = chatGroupService.getById(originChatSession.getTrackId());
        if(StringUtils.isNotEmpty(chatSession.getTitle())) chatGroup.setTitle(originChatSession.getTitle());
        if(StringUtils.isNotEmpty(chatSession.getAvatar())) chatGroup.setAvatar(originChatSession.getAvatar());
        chatGroupService.updateById(chatGroup);

        //发送系统消息
        chatService.chat(new ChatMessage.ChatMessageBuilder<SystemMessage>(originChatSession.getId(),
                new SystemMessage.Builder().text(
                        StringUtils.isNotEmpty(chatSession.getTitle()) ? sohoUserDetails.getUsername() + " 修改了群名 " + chatSession.getTitle() : sohoUserDetails.getUsername() + " 管理员更新了群头像"
                ).build()).build());

        //通知客户端刷新会话
        chatService.send2Session(chatSession, ChatMessage.builder().toSessionId(chatSession.getId()).fromUid(0l)
                .message(RealTimeCmd.builder().name("refreshSessionList").params(null).build()).build());

        return R.success(Boolean.TRUE);
    }

    /**
     * 获取会话用户信息
     *
     * @param sessionId
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/sessionUser")
    public R<ChatSessionUser> sessionUser(Long sessionId, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getUserId, sohoUserDetails.getId())
                .eq(ChatSessionUser::getSessionId, sessionId);
        ChatSessionUser oldChatSessionUser = chatSessionUserService.getOne(lambdaQueryWrapper);
        return R.success(oldChatSessionUser);
    }

    /**
     * 用户退出会话
     *
     * @param chatSessionUser
     * @param sohoUserDetails
     * @return
     */
    @DeleteMapping("/exitSession")
    public R<Boolean> exitSession(@RequestBody ChatSessionUser chatSessionUser, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        Assert.notNull(chatSessionUser.getSessionId(), "会话ID不能为空");
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getSessionId, chatSessionUser.getSessionId())
                .eq(ChatSessionUser::getUserId, sohoUserDetails.getId());
        return R.success(chatSessionUserService.remove(lambdaQueryWrapper));
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

    /**
     * 上传聊天文件/图片
     *
     * @param file
     * @return
     */
    @PostMapping("/image")
    public R<String> image(@RequestParam(value = "upload")MultipartFile file) {
        try {
            MimeType mimeType = MimeTypeUtils.parseMimeType(file.getContentType());
            if(!mimeType.getType().equals("image")) {
                return R.error("请传递正确的图片格式");
            }
            //TODO 配置正确的文件路径
            String url = UploadUtils.upload("user/avatar", file);
            return R.success(url);
        } catch (Exception ioException) {
            log.error(ioException.toString());
            ioException.printStackTrace();
            return R.error("文件上传失败");
        }
    }


    /**
     * 上传聊天文件/图片
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<HashMap<String, Object>> upload(@RequestParam(value = "upload")MultipartFile file) {
        try {
            UploadInfoVo uploadInfoVo = upload.save(file);

            HashMap<String, Object> result = new HashMap<>();
//            //不做文件扩展名验证
//            //TODO 配置正确的文件路径
//            String url = UploadUtils.upload("user/upload", file);

            result.put("url", uploadInfoVo.getUrl());
            result.put("size", uploadInfoVo.getSize());
            result.put("name", file.getOriginalFilename());
            return R.success(result);
        } catch (Exception ioException) {
            log.error(ioException.toString());
            ioException.printStackTrace();
            return R.error("文件上传失败");
        }
    }

    /**
     * 获取会话用户信息
     *
     * @param sessionId
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/sessionUsers")
    public R<List<SessionUserVO>> sessionUserList(Long sessionId, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getSessionId, sessionId);
        lambdaQueryWrapper.eq(ChatSessionUser::getStatus, ChatSessionEnums.Status.ACTIVE.getId());
        List<ChatSessionUser> list = chatSessionUserService.list(lambdaQueryWrapper);
        //获取用户信息
        List<Long> uids = list.stream().map(ChatSessionUser::getUserId).collect(Collectors.toList());
        List<ChatUser> users = chatUserService.getBaseMapper().selectBatchIds(uids);
        Map<Long,ChatUser> userMap = users.stream().collect(Collectors.toMap(ChatUser::getId, item->item));
        //获取好友信息
        LambdaQueryWrapper<ChatUserFriend> chatUserFriendLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatUserFriendLambdaQueryWrapper.eq(ChatUserFriend::getChatUid, sohoUserDetails.getId())
                .in(ChatUserFriend::getFriendUid, uids);
        Map<Long,ChatUserFriend> friendMap = chatUserFriendService.list(chatUserFriendLambdaQueryWrapper).stream().collect(Collectors.toMap(ChatUserFriend::getFriendUid,item->item));

        //转换数据
        List<SessionUserVO> results = list.stream().map(item->{
            SessionUserVO sessionUserVO = BeanUtils.copy(item, SessionUserVO.class);
            ChatUser chatUser = userMap.get(item.getUserId());
            sessionUserVO.setAvatar(chatUser.getAvatar());
            sessionUserVO.setUsername(chatUser.getUsername());
            sessionUserVO.setNickname(chatUser.getNickname());
            ChatUserFriend chatUserFriend = friendMap.get(item.getUserId());
            if(chatUserFriend != null) {
                sessionUserVO.setNotesName(chatUserFriend.getNotesName());
            }
            return sessionUserVO;
        }).collect(Collectors.toList());

        return R.success(results);
    }

    /**
     * 批量更新消息通知设置
     *
     * @param batchUpdateNotificationReq
     * @param sohoUserDetails
     * @return
     */
    @PutMapping("/bachUpdateNotification")
    public R<Boolean> batchUpdateNotification(@RequestBody BatchUpdateNotificationReq batchUpdateNotificationReq, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        Assert.notNull(batchUpdateNotificationReq.getType(), "消息提示类型不能为空");
        Assert.notNull(batchUpdateNotificationReq.getSessionIds(), "请传递会话ID");
        Assert.isTrue(batchUpdateNotificationReq.getSessionIds().size()>0, "请传递会话ID");

        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ChatSessionUser::getSessionId, batchUpdateNotificationReq.getSessionIds())
                .eq(ChatSessionUser::getUserId, sohoUserDetails.getId());
        //批量更新
        ChatSessionUser chatSessionUser = new ChatSessionUser();
        switch (batchUpdateNotificationReq.getType()) {
            case 1:
                chatSessionUser.setIsNotDisturb(0);
                chatSessionUser.setIsShield(0);
                break;
            case 2:
                chatSessionUser.setIsNotDisturb(1);
                chatSessionUser.setIsShield(0);
                break;
            case 3:
                chatSessionUser.setIsNotDisturb(1);
                chatSessionUser.setIsShield(1);
                break;
        }
        chatSessionUserService.getBaseMapper().update(chatSessionUser, lambdaQueryWrapper);
        return R.success(true);
    }

    /**
     * 撤回指定消息
     *
     * @param id
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/revoke")
    public R<Boolean> revoke(String id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<ChatSessionMessage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionMessage::getClientMessageId, id);
        lambdaQueryWrapper.last("limit 1");
        ChatSessionMessage chatMessage = chatSessionMessageService.getOne(lambdaQueryWrapper);
        //检查消息是否超时
        Integer revokeTimeout = adminConfigApiService.getByKey("chat-session-message-revoke-timeout", Integer.class, 120);
        LocalDateTime future = chatMessage.getCreatedTime().plusSeconds(revokeTimeout);

        if(future.compareTo(LocalDateTime.now())<0) {
            return R.error("超时；无法撤回消息");
        }

        //检查消息是否存在
        Assert.notNull(chatMessage, "消息不存在");

        if(!chatMessage.getFromUid().equals(sohoUserDetails.getId())) {
            throw new RuntimeException("不能撤销他人消息");
        }

        //删除消息关联
        chatMessage.setIsDeleted(1);
        chatMessage.setUpdatedTime(LocalDateTime.now());
        chatSessionMessageService.updateById(chatMessage);
        return R.success(true);
    }

    /**
     * 删除指定用户消息
     *
     * @param id
     * @param sohoUserDetails
     * @return
     */
    @DeleteMapping("/deleteUserMessage/{id}")
    public R<Boolean> deleteUserMessage(@PathVariable("id") String id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<ChatSessionMessage> sessionMessageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sessionMessageLambdaQueryWrapper.eq(ChatSessionMessage::getClientMessageId, id)
                .last("limit 1");
        ChatSessionMessage chatSessionMessage = chatSessionMessageService.getOne(sessionMessageLambdaQueryWrapper);
        Assert.notNull(chatSessionMessage, "消息不存在");

        LambdaQueryWrapper<ChatSessionMessageUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionMessageUser::getMessageId, chatSessionMessage.getId())
                .eq(ChatSessionMessageUser::getUid, sohoUserDetails.getId())
                .last("limit 1");
        ChatSessionMessageUser chatSessionMessageUser = chatSessionMessageUserService.getOne(lambdaQueryWrapper);
        Assert.notNull(chatSessionMessageUser, "消息不存在");
        chatSessionMessageUserService.removeById(chatSessionMessageUser);
        return R.success(true);
    }
}
