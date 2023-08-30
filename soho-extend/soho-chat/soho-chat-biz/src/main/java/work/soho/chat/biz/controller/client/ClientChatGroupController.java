package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import work.soho.chat.biz.domain.ChatGroup;
import work.soho.chat.biz.domain.ChatGroupApply;
import work.soho.chat.biz.domain.ChatGroupUser;
import work.soho.chat.biz.domain.ChatUserNotice;
import work.soho.chat.biz.enums.ChatGroupUserEnums;
import work.soho.chat.biz.enums.ChatUserNoticeEnums;
import work.soho.chat.biz.service.ChatGroupApplyService;
import work.soho.chat.biz.service.ChatGroupService;
import work.soho.chat.biz.service.ChatGroupUserService;
import work.soho.chat.biz.service.ChatUserNoticeService;
import work.soho.chat.biz.vo.ChatGroupVO;
import work.soho.common.core.result.R;
import work.soho.common.core.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 群组客户端控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/chat/chat-group")
public class ClientChatGroupController {
    private final ChatGroupService chatGroupService;

    private final ChatGroupUserService chatGroupUserService;

    private final ChatGroupApplyService chatGroupApplyService;

    private final ChatUserNoticeService chatUserNoticeService;

    @GetMapping("/list")
    public R<PageSerializable<ChatGroup>> list(ChatGroup chatGroup, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        LambdaQueryWrapper<ChatGroup> lqw = new LambdaQueryWrapper<ChatGroup>();
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
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 创建群组
     *
     * @param chatGroup
     * @return
     */
    @PostMapping("")
    public R<Boolean> create(@RequestBody  ChatGroup chatGroup,@RequestBody List<Long> chatUserIds, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        chatGroup.setMasterChatUid(sohoUserDetails.getId());
        chatGroup.setUpdatedTime(LocalDateTime.now());
        chatGroup.setCreatedTime(LocalDateTime.now());
        return R.success(chatGroupService.save(chatGroup));
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
}
