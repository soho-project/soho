package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.biz.domain.ChatGroupApply;
import work.soho.chat.biz.domain.ChatGroupUser;
import work.soho.chat.biz.enums.ChatGroupApplyEnums;
import work.soho.chat.biz.enums.ChatGroupUserEnums;
import work.soho.chat.biz.service.ChatGroupApplyService;
import work.soho.chat.biz.service.ChatGroupService;
import work.soho.chat.biz.service.ChatGroupUserService;
import work.soho.chat.biz.service.ChatSessionService;
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

        return R.success(Boolean.TRUE);
    }
}
