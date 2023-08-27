package work.soho.chat.biz.controller.client;

import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.biz.domain.ChatUserFriendApply;
import work.soho.chat.biz.domain.ChatUserNotice;
import work.soho.chat.biz.enums.ChatUserFriendApplyEnums;
import work.soho.chat.biz.enums.ChatUserNoticeEnums;
import work.soho.chat.biz.service.ChatUserFriendApplyService;
import work.soho.chat.biz.service.ChatUserNoticeService;
import work.soho.common.core.result.R;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/chat/chat/chatUserFriendApply")
@RequiredArgsConstructor
public class ClientChatUserFriendApplyController {
    private final ChatUserFriendApplyService chatUserFriendApplyService;

    private final ChatUserNoticeService chatUserNoticeService;


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

        return R.success(true);
    }
}
