package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.api.ChatMessage;
import work.soho.chat.api.payload.SystemMessage;
import work.soho.chat.biz.domain.*;
import work.soho.chat.biz.enums.ChatGroupApplyEnums;
import work.soho.chat.biz.enums.ChatGroupUserEnums;
import work.soho.chat.biz.enums.ChatSessionEnums;
import work.soho.chat.biz.service.*;
import work.soho.common.core.result.R;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat/chat/chatGroupApply")
public class ClientChatGroupApplyController {
    private final ChatGroupApplyService chatGroupApplyService;

    private final ChatGroupUserService chatGroupUserService;

    private final ChatGroupService chatGroupService;

    private final ChatSessionService chatSessionService;

    private final ChatSessionUserService chatSessionUserService;

    private final ChatService chatService;

    private final ChatUserService chatUserService;

    /**
     * 审核用户加群
     *
     * @param id
     * @param sohoUserDetails
     * @return
     */
    @GetMapping
    public R<Boolean> agree(Long id, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatGroupApply chatGroupApply = chatGroupApplyService.getById(id);
        Assert.notNull(chatGroupApply, "审批单不存在");
        //检查用户是否为审批群的管理员
        LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatGroupUser::getGroupId, chatGroupApply.getGroupId());
        lambdaQueryWrapper.eq(ChatGroupUser::getChatUid, sohoUserDetails.getId());
        lambdaQueryWrapper.eq(ChatGroupUser::getIsAdmin, ChatGroupUserEnums.IsAdmin.YES.getId());
        ChatGroupUser chatGroupUser = chatGroupUserService.getOne(lambdaQueryWrapper);
        Assert.notNull(chatGroupUser, "非管理员不能操作");
        //修改申请单状态
        chatGroupApply.setStatus(ChatGroupApplyEnums.Status.AGREE.getId());
        chatGroupApply.setUpdatedTime(LocalDateTime.now());
        chatGroupApplyService.updateById(chatGroupApply);
        //用户加入群聊
        ArrayList<Long> groupUids = new ArrayList<>();
        groupUids.add(chatGroupApply.getChatUid());
        chatGroupService.joinGroup(chatGroupApply.getGroupId(), groupUids);

        //获取会话
        ChatSession chatSession = chatSessionService.findSession(ChatSessionEnums.Type.GROUP_CHAT, chatGroupApply.getGroupId());

        //加入session user
        ChatSessionUser chatSessionUser = new ChatSessionUser();
        ChatUser chatUser = chatUserService.getById(chatGroupApply.getChatUid());
        chatSessionUser.setUserId(chatGroupApply.getChatUid());
        chatSessionUser.setSessionId(chatSession.getId());
        chatSessionUser.setUpdatedTime(LocalDateTime.now());
        chatSessionUser.setCreatedTime(LocalDateTime.now());
        chatSessionUserService.save(chatSessionUser);

        //发送系统通知
        chatService.chat(new ChatMessage.Builder<SystemMessage>(chatSession.getId(), new SystemMessage.Builder().text(chatUser.getUsername() +" 加入群聊").build()).build());

        return R.success(Boolean.TRUE);
    }
}
