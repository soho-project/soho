package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.chat.biz.domain.*;
import work.soho.chat.biz.enums.ChatGroupApplyEnums;
import work.soho.chat.biz.enums.ChatGroupUserEnums;
import work.soho.chat.biz.enums.ChatSessionEnums;
import work.soho.chat.biz.enums.ChatUserNoticeEnums;
import work.soho.chat.biz.req.CreateGroupReq;
import work.soho.chat.biz.service.*;
import work.soho.chat.biz.vo.ChatGroupVO;
import work.soho.chat.biz.vo.GroupVO;
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
    public R<PageSerializable<ChatGroup>> myList(ChatGroup chatGroup, BetweenCreatedTimeRequest betweenCreatedTimeRequest,@AuthenticationPrincipal SohoUserDetails sohoUserDetails)
    {
        //查询我的群组的IDS
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getChatUid, sohoUserDetails.getId());
        List<Long> groupIds = chatGroupUserService.list(lambdaQueryWrapper).stream().map(ChatGroupUser::getGroupId).collect(Collectors.toList());
        if(groupIds.isEmpty()) {
            return R.success(new PageSerializable<>(new ArrayList<>()));
        }

        LambdaQueryWrapper<ChatGroup> lqw = new LambdaQueryWrapper<ChatGroup>();
        lqw.in(ChatGroup::getId ,groupIds);
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

        return R.success(new PageSerializable<>(list));
    }

    /**
     * 创建群组
     *
     * @param createGroupReq
     * @return
     */
    @PostMapping("")
    public R<Boolean> create(@RequestBody CreateGroupReq createGroupReq, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroup chatGroup = new ChatGroup();
        chatGroup.setTitle(createGroupReq.getTitle());
        chatGroup.setMasterChatUid(sohoUserDetails.getId());
        chatGroup.setUpdatedTime(LocalDateTime.now());
        chatGroup.setCreatedTime(LocalDateTime.now());
        chatGroupService.save(chatGroup);

        //TODO 创建群组用户数据
        List<Long> chatUserIds = createGroupReq.getChatUserIds();
        chatUserIds.add(chatGroup.getMasterChatUid());
        List<ChatUser> users = chatUserService.getBaseMapper().selectBatchIds(chatUserIds);
        users.forEach(user->{
            ChatGroupUser chatGroupUser = new ChatGroupUser();
            chatGroupUser.setGroupId(chatGroup.getId());
            chatGroupUser.setChatUid(sohoUserDetails.getId());
            chatGroupUser.setIsAdmin(ChatGroupUserEnums.IsAdmin.NO.getId());
            chatGroupUser.setNickname(StringUtils.isEmpty(user.getNickname()) ? user.getUsername() : user.getNickname());
            chatGroupUser.setUpdatedTime(LocalDateTime.now());
            chatGroupUser.setCreatedTime(LocalDateTime.now());
            chatGroupUserService.save(chatGroupUser);
        });

        return R.success(true);
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
     * 退出群组
     *
     * @param id
     * @param sohoUserDetails
     * @return
     */
    @DeleteMapping("/exit/{id}")
    public R<Boolean> exitGroup(@PathVariable Long id,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        chatGroupService.exitGroup(id, sohoUserDetails.getId());
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
     * 申请入群
     *
     * @param sohoUserDetails
     * @return
     */
    @PostMapping("/apply")
    public R<ChatGroupApply> apply(@RequestBody ChatGroupApply chatGroupApply, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroup chatGroup = chatGroupService.getById(chatGroupApply.getGroupId());
        Assert.notNull(chatGroup);
        //TODO 检查当前用户不在群里面
        chatGroupApply.setUpdatedTime(LocalDateTime.now());
        chatGroupApply.setCreatedTime(LocalDateTime.now());
        chatGroupApply.setChatUid(sohoUserDetails.getId());
        chatGroupApply.setStatus(ChatGroupApplyEnums.Status.PENDING_PROCESSING.getId());
        chatGroupApplyService.save(chatGroupApply);

        //获取所有的群管理
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getGroupId, chatGroup.getId())
                .eq(ChatGroupUser::getIsAdmin, ChatGroupUserEnums.IsAdmin.YES.getId());
        List<ChatGroupUser> groupUsers = chatGroupUserService.list(lambdaQueryWrapper);

        //创建发送通知
        ChatUserNotice chatUserNotice = new ChatUserNotice();
        chatUserNotice.setChatUid(sohoUserDetails.getId());
        chatUserNotice.setStatus(ChatUserNoticeEnums.Status.PENDING_PROCESSING.getId());
        chatUserNotice.setType(ChatUserNoticeEnums.Type.GROUP.getType());
        chatUserNotice.setTrackingId(chatGroupApply.getId());
        chatUserNotice.setUpdatedTime(LocalDateTime.now());
        chatUserNotice.setCreatedTime(LocalDateTime.now());
        chatUserNoticeService.save(chatUserNotice);

        //给管理员发送通知
        groupUsers.forEach(item->{
            //创建发送通知
            ChatUserNotice notice = new ChatUserNotice();
            notice.setChatUid(item.getChatUid());
            notice.setStatus(ChatUserNoticeEnums.Status.PENDING_PROCESSING.getId());
            notice.setType(ChatUserNoticeEnums.Type.GROUP.getType());
            notice.setTrackingId(chatGroupApply.getId());
            notice.setUpdatedTime(LocalDateTime.now());
            notice.setCreatedTime(LocalDateTime.now());
            chatUserNoticeService.save(notice);
        });

        return R.success(chatGroupApply);
    }

    /**
     * 处理申请
     *
     * @param chatGroupApply
     * @param sohoUserDetails
     * @return
     */
    @PutMapping("/doApply")
    public R<Boolean> doApply(@RequestBody ChatGroupApply chatGroupApply,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroupApply oldGroupApply = chatGroupApplyService.getById(chatGroupApply.getId());
        //TODO 检查当前用户是否为管理员
        LambdaQueryWrapper<ChatGroupUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ChatGroupUser::getChatUid, sohoUserDetails.getId());
        lqw.eq(ChatGroupUser::getGroupId, oldGroupApply.getGroupId());
        lqw.eq(ChatGroupUser::getIsAdmin, ChatGroupUserEnums.IsAdmin.YES.getId());
        ChatGroupUser chatGroupUser = chatGroupUserService.getOne(lqw);
        Assert.notNull(chatGroupUser);
        //审核加群申请
        oldGroupApply.setStatus(chatGroupApply.getStatus());
        return R.success(chatGroupApplyService.updateById(oldGroupApply));
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


}
