package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.chat.api.ChatMessage;
import work.soho.chat.api.payload.SystemMessage;
import work.soho.chat.biz.domain.*;
import work.soho.chat.biz.enums.*;
import work.soho.chat.biz.req.*;
import work.soho.chat.biz.service.*;
import work.soho.chat.biz.vo.ChatGroupVO;
import work.soho.chat.biz.vo.GroupQuestionsVo;
import work.soho.chat.biz.vo.GroupVO;
import work.soho.chat.biz.vo.MyGroupVO;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.upload.utils.UploadUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 群组客户端控制器
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/chat/chat-group")
public class ClientChatGroupController {
    private final ChatUserService chatUserService;

    private final ChatGroupService chatGroupService;

    private final ChatGroupUserService chatGroupUserService;

    private final ChatGroupApplyService chatGroupApplyService;

    private final ChatUserNoticeService chatUserNoticeService;

    private final ChatSessionService chatSessionService;

    private final ChatSessionUserService chatSessionUserService;

    private final ChatService chatService;

    private final ChatGroupQuestionsService chatGroupQuestionsService;

    /**
     * 群组列表/群组搜索用接口
     *
     * @param chatGroup
     * @param keyword
     * @param betweenCreatedTimeRequest
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/list")
    public R<PageSerializable<GroupVO>> list(ChatGroup chatGroup,String keyword, BetweenCreatedTimeRequest betweenCreatedTimeRequest,@AuthenticationPrincipal SohoUserDetails sohoUserDetails)
    {
        LambdaQueryWrapper<ChatGroup> lqw = new LambdaQueryWrapper<ChatGroup>();
        //关键字搜索
        if(StringUtils.isNotBlank(keyword)) {
            lqw.like(ChatGroup::getTitle, keyword + "%");
            lqw.or().like(ChatGroup::getId, keyword);
        }
        lqw.eq(chatGroup.getId() != null, ChatGroup::getId ,chatGroup.getId());
        lqw.like(StringUtils.isNotBlank(chatGroup.getTitle()),ChatGroup::getTitle ,chatGroup.getTitle());
        lqw.eq(chatGroup.getType() != null, ChatGroup::getType ,chatGroup.getType());
        lqw.eq(chatGroup.getMasterChatUid() != null, ChatGroup::getMasterChatUid ,chatGroup.getMasterChatUid());
        lqw.like(StringUtils.isNotBlank(chatGroup.getAvatar()),ChatGroup::getAvatar ,chatGroup.getAvatar());
        lqw.like(StringUtils.isNotBlank(chatGroup.getIntroduction()),ChatGroup::getIntroduction ,chatGroup.getIntroduction());
        lqw.like(StringUtils.isNotBlank(chatGroup.getProclamation()),ChatGroup::getProclamation ,chatGroup.getProclamation());
        lqw.eq(chatGroup.getUpdatedTime() != null, ChatGroup::getUpdatedTime ,chatGroup.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ChatGroup::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ChatGroup::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ChatGroup> list = chatGroupService.list(lqw);

        //计算已经加入的群
        List<Long> resultGroupIds = list.stream().map(ChatGroup::getId).collect(Collectors.toList());
        resultGroupIds.add(0l);
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.in(ChatGroupUser::getGroupId, resultGroupIds)
                .eq(ChatGroupUser::getChatUid, sohoUserDetails.getId());
        List<ChatGroupUser> groupUsers = chatGroupUserService.list(lambdaQueryWrapper1);
        List<GroupVO> result = new ArrayList<>();
        list.forEach(item->{
            GroupVO chatGroupVO = BeanUtils.copy(item, GroupVO.class);
            Optional<ChatGroupUser> optional = groupUsers.stream().filter(u->{return u.getGroupId().equals(item.getId());}).findFirst();
            chatGroupVO.setInGroup(optional.isPresent());
            result.add(chatGroupVO);
        });

        return R.success(new PageSerializable<>(result));
    }

    /**
     * 我的群列表
     *
     * @param chatGroup
     * @param betweenCreatedTimeRequest
     * @return
     */
    @GetMapping("/my-list")
    public R<PageSerializable<MyGroupVO>> myList(ChatGroup chatGroup, BetweenCreatedTimeRequest betweenCreatedTimeRequest,@AuthenticationPrincipal SohoUserDetails sohoUserDetails)
    {
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getChatUid, sohoUserDetails.getId());
        List<ChatGroupUser> groupUsers = chatGroupUserService.list(lambdaQueryWrapper);
        List<Long> groupIds = groupUsers.stream().map(ChatGroupUser::getGroupId).collect(Collectors.toList());
        groupIds.add(0l);

        LambdaQueryWrapper<ChatGroup> lqw = new LambdaQueryWrapper<ChatGroup>();
        lqw.in(ChatGroup::getId, groupIds);
        List<ChatGroup> list = chatGroupService.list(lqw);

        //计算已经加入的群
        List<MyGroupVO> result = new ArrayList<>();
        list.forEach(item->{
            MyGroupVO chatGroupVO = BeanUtils.copy(item, MyGroupVO.class);
            Optional<ChatGroupUser> optional = groupUsers.stream().filter(u->{return u.getGroupId().equals(item.getId());}).findFirst();
            if(optional.isPresent()) {
                chatGroupVO.setNotesName(optional.get().getNotesName());
                chatGroupVO.setIsAdmin(optional.get().getIsAdmin());
                if(chatGroupVO.getIsAdmin() == 0 && item.getMasterChatUid().equals(sohoUserDetails.getId())) {
                    chatGroupVO.setIsAdmin(1);
                }
            }
            result.add(chatGroupVO);
        });
        return R.success(new PageSerializable<>(result));
    }

    /**
     * 创建群组
     *
     * @param createGroupReq
     * @return
     */
    @PostMapping("")
    public R<ChatSession> create(@RequestBody CreateGroupReq createGroupReq, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroup chatGroup = new ChatGroup();
        chatGroup.setTitle(createGroupReq.getTitle());
        chatGroup.setMasterChatUid(sohoUserDetails.getId());
        chatGroup.setUpdatedTime(LocalDateTime.now());
        chatGroup.setCreatedTime(LocalDateTime.now());
        List<Long> chatUserIds = createGroupReq.getChatUserIds();
        chatGroupService.createGroup(chatGroup, chatUserIds);

        //同步群组数据到会话
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getGroupId, chatGroup.getId());
        List<ChatGroupUser> chatGroupUsers = chatGroupUserService.list(lambdaQueryWrapper);

        //创建会话信息
        ChatSession chatSession = chatSessionService.groupSession(chatGroup, chatGroupUsers);

        //发送系统通知
        chatService.chat(new ChatMessage.Builder<SystemMessage>(chatSession.getId(), new SystemMessage.Builder().text("创建群聊： " + chatSession.getTitle()).build()).build());

        return R.success(chatSession);
    }

    /**
     * 邀请用户入群
     *
     * @param inviteJoinGroupReq
     * @param sohoUserDetails
     * @return
     */
    @PostMapping("/inviteJoinGroup")
    public R<ChatSession> inviteJoinGroup(@RequestBody InviteJoinGroupReq inviteJoinGroupReq,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        Assert.notNull(inviteJoinGroupReq.getSessionId(), "请正确传递参数");
        Assert.isTrue(inviteJoinGroupReq.getChatUserIds()!=null && inviteJoinGroupReq.getChatUserIds().size()>0, "请传递邀请用户ID");
        ChatSession chatSession = chatSessionService.getById(inviteJoinGroupReq.getSessionId());
        Assert.isTrue(chatSession.getType() == ChatSessionEnums.Type.GROUP_CHAT.getId(), "非群聊不能邀请加群");
        Long groupId = chatSession.getTrackId();
        ChatGroup chatGroup = chatGroupService.getById(groupId);

        //加入群组
        chatGroupService.joinGroup(chatGroup.getId(), inviteJoinGroupReq.getChatUserIds());

        //同步用户到会话
        //同步群组数据到会话
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getGroupId, chatGroup.getId());
        lambdaQueryWrapper.in(ChatGroupUser::getChatUid, inviteJoinGroupReq.getChatUserIds());
        List<ChatGroupUser> chatGroupUsers = chatGroupUserService.list(lambdaQueryWrapper);
        //创建/同步群信息到会话
        chatSession = chatSessionService.groupSession(chatGroup, chatGroupUsers);
        //统计会话用户信息
        chatSessionService.syncInfo(chatSession.getId());
        //发送邀请用户进入通知
        ChatSession finalChatSession = chatSession;
        chatGroupUsers.forEach(item->{
            String msgText = item.getNickname() + " 加入群";
            chatService.chat(new ChatMessage.Builder<SystemMessage>(finalChatSession.getId(), new SystemMessage.Builder().text(msgText).build()).build());
        });

        return R.success(chatSession);
    }


    /**
     * 更新群信息
     *
     * @param chatGroup
     * @param sohoUserDetails
     * @return
     */
    @PutMapping("")
    public R<Boolean> update(@RequestBody ChatGroup chatGroup,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        Assert.notNull(chatGroupService.getChatGroupUser(chatGroup.getId(), sohoUserDetails.getId()), "无权访问");
        return R.success(chatGroupService.updateById(chatGroup));
    }

    /**
     * 更新群组用户信息
     *
     * @param chatGroupUser
     * @param sohoUserDetails
     * @return
     */
    @PutMapping("/groupUser")
    public R<Boolean> updateGroupUser(@RequestBody ChatGroupUser chatGroupUser,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        Assert.notNull(chatGroupUser.getGroupId(), "组ID不能为空");
        if(chatGroupUser.getChatUid()!=null) {
            return R.error("无权访问他人数据");
        }
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getChatUid, sohoUserDetails.getId())
                .eq(ChatGroupUser::getGroupId, chatGroupUser.getGroupId());
        ChatGroupUser oldChatGroupUser = chatGroupUserService.getOne(lambdaQueryWrapper);

        //同步备注名
        if(chatGroupUser.getNotesName() != null) {
            ChatSession chatSession = chatSessionService.findSession(ChatSessionEnums.Type.GROUP_CHAT, oldChatGroupUser.getGroupId());
            if(chatSession != null) {
                ChatSessionUser chatSessionUser = chatSessionUserService.getSessionUser(chatSession.getId(), sohoUserDetails.getId());
                chatSessionUser.setTitle(chatGroupUser.getNotesName());
                chatSessionUser.setUpdatedTime(LocalDateTime.now());
                chatSessionUserService.updateById(chatSessionUser);
            }
        }

        chatGroupUser.setId(oldChatGroupUser.getId());
        chatGroupUser.setUpdatedTime(LocalDateTime.now());
        chatGroupUser.setCreatedTime(LocalDateTime.now());
        return R.success(chatGroupUserService.updateById(chatGroupUser));
    }

    /**
     * 获取群组详情
     *
     * @param id
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/{id}")
    public R<ChatGroupVO> details(@PathVariable Long id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroup chatGroup = chatGroupService.getById(id);
        //检查群是否为用户群
        ChatGroupUser chatGroupUser = chatGroupUserService.getByUid(chatGroup.getId(), sohoUserDetails.getId());
        Assert.notNull(chatGroupUser, "无权访问");
        return R.success(chatGroupService.getDetails(id, sohoUserDetails.getId()));
    }

    /**
     * 获取自己在指定群的信息
     *
     * @param id
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/myGroupUser/{id}")
    public R<ChatGroupUser> myGroupUser(@PathVariable Long id,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroup chatGroup = chatGroupService.getById(id);
        Assert.notNull(chatGroup, "群不存在");
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getGroupId, chatGroup.getId())
                .eq(ChatGroupUser::getChatUid, sohoUserDetails.getId());
        ChatGroupUser chatGroupUser = chatGroupUserService.getOne(lambdaQueryWrapper);
        return R.success(chatGroupUser);
    }

    /**
     * 群组用户信息
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/groupUserList/{id}")
    public R<List<ChatGroupUser>> groupUserList(@PathVariable Long id,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroup chatGroup = chatGroupService.getById(id);
        Assert.notNull(chatGroup, "群不存在");
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getGroupId, id);
        List<ChatGroupUser> list = chatGroupUserService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    /**
     * 退出群组
     *
     * @param id
     * @param sohoUserDetails
     * @return
     */
    @DeleteMapping("/exit/{id}")
    public R<Boolean> exitGroup(@PathVariable Long id,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroup chatGroup = chatGroupService.getById(id);
        if(chatGroup.equals(sohoUserDetails.getId())) {
            return R.error("群主不能退出群聊");
        }

        chatGroupService.exitGroup(id, sohoUserDetails.getId());
        //获取会话
        ChatSession session = chatSessionService.findSession(ChatSessionEnums.Type.GROUP_CHAT, id);
        ChatSessionUser chatSessionUser = chatSessionUserService.getSessionUser(session.getId(), sohoUserDetails.getId());
        chatSessionUser.setStatus(ChatSessionUserEnums.Status.DELETED.getId());
        chatSessionUser.setUpdatedTime(LocalDateTime.now());
        chatSessionUserService.updateById(chatSessionUser);
        //更新会话统计信息
        chatSessionService.syncInfo(chatSessionUser.getSessionId());
        //TODO 发送消息给管理员，告知用户退出
        return R.success(true);
    }

    /**
     * 加入群组
     *
     * @param id
     * @param sohoUserDetails
     * @return
     */
    @PostMapping("/join/{id}")
    public R<Boolean> joinGroup(@PathVariable Long id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        List<Long> uids = new ArrayList<>();
        uids.add(sohoUserDetails.getId());
        chatGroupService.joinGroup(id, uids);
        return R.success(true);
    }

    /**
     * 上传聊天文件/图片
     *
     * @param file
     * @return
     */
    @PostMapping("/avatar/{id}")
    public R<String> avatar(@RequestParam(value = "upload") MultipartFile file,@PathVariable Long id) {
        try {
            //获取群组信息
            ChatGroup chatGroup = chatGroupService.getById(id);
            Assert.notNull(chatGroup, "没有找到对应的群组");
            //TODO 检查用户权限
            MimeType mimeType = MimeTypeUtils.parseMimeType(file.getContentType());
            if(!mimeType.getType().equals("image")) {
                return R.error("请传递正确的图片格式");
            }
            String url = UploadUtils.upload("group/avatar", file);

            chatGroup.setAvatar(url);
            chatGroupService.updateById(chatGroup);
            //同步到会话
            LambdaQueryWrapper<ChatSession> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ChatSession::getType, ChatSessionEnums.Type.GROUP_CHAT.getId())
                    .eq(ChatSession::getTrackId, chatGroup.getId());
            ChatSession chatSession = chatSessionService.getOne(lambdaQueryWrapper);
            if(chatSession != null) {
                chatSession.setAvatar(url);
                chatSessionService.updateById(chatSession);
            }

            return R.success(url);
        } catch (Exception ioException) {
            log.error(ioException.toString());
            ioException.printStackTrace();
            return R.error("文件上传失败");
        }
    }


    /**
     * 对应群的认证信息
     *
     * @param id
     * @return
     */
    @GetMapping("/questions")
    public R<GroupQuestionsVo> questions(Long id,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroup chatGroup = chatGroupService.getById(id);

        //检查请求用户是否为群管理
        boolean inGroup = chatGroupService.isAdmin(id, sohoUserDetails.getId());

        GroupQuestionsVo groupQuestionsVo = new GroupQuestionsVo();
        groupQuestionsVo.setAuthType(chatGroup.getAuthJoinType());
        LambdaQueryWrapper<ChatGroupQuestions> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupQuestions::getGroupId, chatGroup.getId());
        chatGroupQuestionsService.list(lambdaQueryWrapper).forEach(item->{
            GroupQuestionsVo.Questions questions = BeanUtils.copy(item, GroupQuestionsVo.Questions.class);
            if(!inGroup) {
                questions.setAnswer(null);
            }
            groupQuestionsVo.getQuestionsList().add(questions);
        });

        return R.success(groupQuestionsVo);
    }

    /**
     * 修改群认证方式
     *
     * @return
     */
    @PutMapping("/questions")
    public R<Boolean> updateQuestions(@RequestBody UpdateGroupAuthReq updateGroupAuthReq,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroup chatGroup = chatGroupService.getById(updateGroupAuthReq.getGroupId());
        Assert.notNull(chatGroup, "群组不存在");
        chatGroup.setAuthJoinType(updateGroupAuthReq.getAuthType());
        chatGroupService.updateById(chatGroup);
        //检查当前用户是否为管理员
        Assert.isTrue(chatGroupService.isAdmin(chatGroup.getId(), sohoUserDetails.getId()), "无权修改");
        //清理老数据
        LambdaQueryWrapper<ChatGroupQuestions> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupQuestions::getGroupId, chatGroup.getId());
        chatGroupQuestionsService.remove(lambdaQueryWrapper);

        //添加提问
        updateGroupAuthReq.getQuestionsList().forEach(item->{
            ChatGroupQuestions chatGroupQuestions = BeanUtils.copy(item, ChatGroupQuestions.class);
            chatGroupQuestions.setGroupId(chatGroup.getId());
            chatGroupQuestions.setUpdatedTime(LocalDateTime.now());
            chatGroupQuestions.setCreatedTime(LocalDateTime.now());
            chatGroupQuestionsService.save(chatGroupQuestions);
        });

        return R.success(true);
    }

    /**
     * 更新群用户昵称
     *
     * 接口修改权限为 1 本人  2 群管理员
     *
     * @param updateGroupNotesNameReq
     * @param sohoUserDetails
     * @return
     */
    @PutMapping("/updateGroupNotesName")
    public R<Boolean> updateNotesName(@RequestBody UpdateGroupNotesNameReq updateGroupNotesNameReq, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        try {
            ChatGroup chatGroup = chatGroupService.getById(updateGroupNotesNameReq.getGroupId());
            Assert.notNull(chatGroup, "没有找到群");
            ChatGroupUser chatGroupUser = chatGroupService.getChatGroupUser(chatGroup.getId(), updateGroupNotesNameReq.getUid());
            Assert.notNull(chatGroupUser, "用户不存在");
            if(!chatGroupUser.getChatUid().equals(sohoUserDetails.getId())) {
                //检查当前用户是否为该群的管理员
                ChatGroupUser adminChatGroupUser = chatGroupService.getChatGroupUser(chatGroup.getId(), sohoUserDetails.getId());
                Assert.notNull(adminChatGroupUser, "您不是管理员");
                if(adminChatGroupUser.getIsAdmin() == ChatGroupUserEnums.IsAdmin.NO.getId()) {
                    throw new RuntimeException("无权操作");
                }
            }
            //更新群用户信息
            chatGroupUser.setNotesName(updateGroupNotesNameReq.getNotesName());
            chatGroupUser.setUpdatedTime(LocalDateTime.now());
            chatGroupUserService.updateById(chatGroupUser);
            //同步到会话中心
            List<ChatGroupUser> updateGroupUsers = new ArrayList<>();
            updateGroupUsers.add(chatGroupUser);
            chatSessionService.groupSession(chatGroup, updateGroupUsers);

            return R.success(true);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 更新设置群管理员
     *
     * @param updateGroupAdminReq
     * @param sohoUserDetails
     * @return
     */
    @PutMapping("/updateGroupAdmin")
    public R<Boolean> updateGroupAdmin(@RequestBody UpdateGroupAdminReq updateGroupAdminReq,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroup chatGroup = chatGroupService.getById(updateGroupAdminReq.getGroupId());
        Assert.notNull(chatGroup, "没有找到群");
        ChatGroupUser chatGroupUser = chatGroupService.getChatGroupUser(chatGroup.getId(), updateGroupAdminReq.getUid());
        Assert.notNull(chatGroupUser, "用户不存在");
        //检查当前用户是否为该群的管理员
        ChatGroupUser adminChatGroupUser = chatGroupService.getChatGroupUser(chatGroup.getId(), sohoUserDetails.getId());
        Assert.notNull(adminChatGroupUser, "您不是管理员");
        if(adminChatGroupUser.getIsAdmin() == ChatGroupUserEnums.IsAdmin.NO.getId()) {
            throw new RuntimeException("无权操作");
        }
        //检查是否为超级管理员
        Assert.isTrue(chatGroup.getMasterChatUid().equals(sohoUserDetails.getId()), "无权操作");

        if(updateGroupAdminReq.getIsAdmin() == 1) {
            chatGroupUser.setIsAdmin(ChatGroupUserEnums.IsAdmin.YES.getId());
        } else {
            chatGroupUser.setIsAdmin(ChatGroupUserEnums.IsAdmin.NO.getId());
        }
        chatGroupUser.setUpdatedTime(LocalDateTime.now());
        chatGroupUserService.updateById(chatGroupUser);
        return R.success(true);
    }

    /**
     * 创建群组禁言
     *
     * @param createGroupBannedReq
     * @param sohoUserDetails
     * @return
     */
    @PostMapping("/createGroupBanned")
    public R<Boolean> createGroupBanned(@RequestBody CreateGroupBannedReq createGroupBannedReq,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroup chatGroup = chatGroupService.getById(createGroupBannedReq.getGroupId());
        Assert.notNull(chatGroup, "群组不存在");
        ChatGroupUser requestGroupUser = chatGroupService.getChatGroupUser(chatGroup.getId(), sohoUserDetails.getId());

        ChatGroupUser chatGroupUser = chatGroupService.getChatGroupUser(chatGroup.getId(), createGroupBannedReq.getUid());
        Assert.notNull(chatGroupUser, "用户不存在");

        LocalDateTime now = LocalDateTime.now();
        now.plusSeconds(createGroupBannedReq.getSecond());
        chatGroupUser.setBannedEndTime(now);
        chatGroupUser.setUpdatedTime(LocalDateTime.now());
        chatGroupUserService.updateById(chatGroupUser);

        //同步到会话
        ChatSession chatSession = chatSessionService.findSession(ChatSessionEnums.Type.GROUP_CHAT, chatGroup.getId());
        ChatSessionUser chatSessionUser = chatSessionService.getSessionUser(chatSession.getId(), chatGroupUser.getChatUid());
        chatSessionUser.setCanSend(ChatSessionUserEnums.CanSend.NO.getId());
        chatSessionUser.setUpdatedTime(LocalDateTime.now());
        chatSessionUserService.updateById(chatSessionUser);
        //TODO 调用延时队列恢复会话发送消息能力

        //发送邀请用户进入通知
        ChatSession finalChatSession = chatSession;
        String msgText = chatGroupUser.getNickname() + " 被禁言 " + (createGroupBannedReq.getSecond()/60) + " 分钟";
        chatService.chat(new ChatMessage.Builder<SystemMessage>(finalChatSession.getId(), new SystemMessage.Builder().text(msgText).build()).build());
        return R.success(true);
    }

    /**
     * 删除指定群用户
     *
     * 踢人功能
     *
     * @param groupId
     * @param uid
     * @param sohoUserDetails
     * @return
     */
    @DeleteMapping("/deleteChatUser/{groupId}/{uid}")
    public R<Boolean> deleteChatUser(@PathVariable Long groupId,@PathVariable Long uid,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroup chatGroup = chatGroupService.getById(groupId);
        Assert.notNull(chatGroup, "群不存在");
        //检查请求用户是否为管理员
        Assert.isTrue(chatGroupService.isAdmin(groupId, sohoUserDetails.getId()), "您不是管理员");

        chatGroupService.exitGroup(chatGroup.getId(), uid);
        //获取会话
        ChatSession session = chatSessionService.findSession(ChatSessionEnums.Type.GROUP_CHAT, groupId);
        ChatSessionUser chatSessionUser = chatSessionUserService.getSessionUser(session.getId(), sohoUserDetails.getId());
        chatSessionUser.setStatus(ChatSessionUserEnums.Status.DELETED.getId());
        chatSessionUser.setUpdatedTime(LocalDateTime.now());
        chatSessionUserService.updateById(chatSessionUser);
        //更新会话统计信息
        chatSessionService.syncInfo(chatSessionUser.getSessionId());
        //TODO 发送消息给管理员，告知用户退出
        return R.success(true);
    }
}
