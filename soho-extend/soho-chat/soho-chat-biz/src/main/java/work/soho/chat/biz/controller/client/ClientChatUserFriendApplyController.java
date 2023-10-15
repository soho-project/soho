package work.soho.chat.biz.controller.client;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.api.ChatMessage;
import work.soho.chat.api.payload.SystemMessage;
import work.soho.chat.biz.domain.*;
import work.soho.chat.biz.enums.ChatSessionEnums;
import work.soho.chat.biz.enums.ChatUserFriendApplyEnums;
import work.soho.chat.biz.enums.ChatUserNoticeEnums;
import work.soho.chat.biz.service.*;
import work.soho.common.core.result.R;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
@RequestMapping("/chat/chat/chatUserFriendApply")
@RequiredArgsConstructor
public class ClientChatUserFriendApplyController {
    private final ChatUserFriendApplyService chatUserFriendApplyService;

    private final ChatUserService chatUserService;

    private final ChatUserNoticeService chatUserNoticeService;

    private final ChatUserFriendService chatUserFriendService;

    private final ChatSessionService chatSessionService;

    private final ChatSessionUserService chatSessionUserService;

    private final ChatService chatService;


    /**
     * 发起好友申请
     *
     * @param chatUserFriendApply
     * @param sohoUserDetails
     * @return
     */
    @PostMapping()
    public R<Boolean> created(@RequestBody ChatUserFriendApply chatUserFriendApply, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        chatUserFriendApply.setId(null);
        chatUserFriendApply.setChatUid(sohoUserDetails.getId());
        chatUserFriendApplyService.createApply(chatUserFriendApply);
        return R.success(true);
    }

    /**
     * 审批同意添加好友
     *
     * @param id
     * @param sohoUserDetails
     * @return
     */
    @GetMapping
    public R<Boolean> agree(Long id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatUserFriendApply chatUserFriendApply = chatUserFriendApplyService.getById(id);
        Assert.isTrue(chatUserFriendApply.getStatus() != ChatUserFriendApplyEnums.Status.AGREED.getId(), "非法访问");
        Assert.isTrue(sohoUserDetails.getId().equals(chatUserFriendApply.getFriendUid()), "无权操作");
        //修改状态
        chatUserFriendApply.setStatus(ChatUserFriendApplyEnums.Status.AGREED.getId());
        chatUserFriendApply.setUpdatedTime(LocalDateTime.now());
        chatUserFriendApplyService.updateById(chatUserFriendApply);
        //发送状态
        ChatUserNotice chatUserNotice = new ChatUserNotice();
        chatUserNotice.setType(ChatUserNoticeEnums.Type.FRIEND.getType());
        chatUserNotice.setTrackingId(id);
        chatUserNotice.setChatUid(chatUserFriendApply.getChatUid());
        chatUserNotice.setCreatedTime(LocalDateTime.now());
        chatUserNotice.setCreatedTime(LocalDateTime.now());
        chatUserNoticeService.save(chatUserNotice);
        //添加好友关系
        ChatUser chatUser = chatUserService.getById(chatUserFriendApply.getFriendUid());
        ChatUserFriend chatUserFriend = new ChatUserFriend();
        chatUserFriend.setFriendUid(chatUserFriendApply.getFriendUid());
        chatUserFriend.setChatUid(chatUserFriendApply.getChatUid());
        chatUserFriend.setNotesName(chatUser.getUsername());
        chatUserFriendService.save(chatUserFriend);

        ChatUserFriend chatUserFriend1 = new ChatUserFriend();
        ChatUser chatUser1 = chatUserService.getById(chatUserFriendApply.getChatUid());
        chatUserFriend1.setFriendUid(chatUserFriendApply.getChatUid());
        chatUserFriend1.setChatUid(chatUserFriendApply.getFriendUid());
        chatUserFriend1.setNotesName(chatUser1.getUsername());
        chatUserFriendService.save(chatUserFriend1);

        //创建会话，发送系统信息
        ArrayList<Long> uids = new ArrayList<>();
        uids.add(chatUserFriend1.getChatUid());
        ChatSession chatSession = chatSessionService.createSession(chatUserFriend.getChatUid(), uids, ChatSessionEnums.Type.PRIVATE_CHAT);
        //更新会话别名
        ChatSessionUser chatSessionUser = chatSessionUserService.getSessionUser(chatSession.getId(), chatUserFriend.getChatUid());
        chatSessionUser.setTitle(chatUserFriend.getNotesName());
        chatSessionUserService.updateById(chatSessionUser);
        ChatSessionUser chatSessionUser1 = chatSessionUserService.getSessionUser(chatSession.getId(), chatUserFriend1.getChatUid());
        chatSessionUser1.setTitle(chatUserFriend1.getNotesName());
        chatSessionUserService.updateById(chatSessionUser1);

        //更新会话统计信息
        chatSessionService.syncInfo(chatSession.getId());

        //发送系统通知
        chatService.chat(new ChatMessage.Builder<SystemMessage>(chatSession.getId(), new SystemMessage.Builder().text("添加好友成功").build()).build());
        return R.success(true);
    }
}
