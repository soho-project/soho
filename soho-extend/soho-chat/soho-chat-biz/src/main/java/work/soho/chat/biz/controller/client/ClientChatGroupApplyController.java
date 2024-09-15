package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.chat.api.ChatMessage;
import work.soho.chat.api.payload.SystemMessage;
import work.soho.chat.biz.domain.*;
import work.soho.chat.biz.enums.*;
import work.soho.chat.biz.service.*;
import work.soho.common.core.result.R;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "客户端群申请")
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

    private final ChatGroupQuestionsService chatGroupQuestionsService;

    private final ChatUserNoticeService chatUserNoticeService;

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
        //禁止加群返回
        if(chatGroup.getAuthJoinType() == ChatGroupEnums.AuthJoinType.REFUSE.getId()) {
            return R.error("群禁止加入");
        }
        //TODO 检查当前用户不在群里面
        chatGroupApply.setUpdatedTime(LocalDateTime.now());
        chatGroupApply.setCreatedTime(LocalDateTime.now());
        chatGroupApply.setChatUid(sohoUserDetails.getId());
        chatGroupApply.setStatus(ChatGroupApplyEnums.Status.PENDING_PROCESSING.getId());
        chatGroupApplyService.save(chatGroupApply);
        switch (chatGroup.getAuthJoinType()) {
            case 1: //可以直接入群
                doGroupAddUser(chatGroupApply, null); //直接入群
                break;
            case 2: //需要管理员同意
            case 4: //需要回复问题并且管理员同意
                sendJoinGroupInfo(chatGroup, chatGroupApply, sohoUserDetails);
                break;
            case 3: //需要正确回复问题
                LambdaQueryWrapper<ChatGroupQuestions> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(ChatGroupQuestions::getGroupId, chatGroup.getId());
                lambdaQueryWrapper.last("limit 1");
                ChatGroupQuestions chatGroupQuestions = chatGroupQuestionsService.getOne(lambdaQueryWrapper);
                if(chatGroupQuestions != null
                        && chatGroupQuestions.getQuestions().equals(chatGroupApply.getAsk())
                        && chatGroupQuestions.getAnswer().equals(chatGroupApply.getAnswer())
                ) {
                    //TODO 认证通过直接添加到群里
                    System.out.println("已经正确回复问题， 待直接添加用户到群");
                    doGroupAddUser(chatGroupApply, null);
                    return R.success(chatGroupApply);
                }
                break;
            default:
                return R.error("本群禁止加入");
        }
        //将用户加入群

        return R.success(chatGroupApply);
    }

    /**
     * 用户申请入群通知
     *
     * @param chatGroup
     */
    private void sendJoinGroupInfo(ChatGroup chatGroup,ChatGroupApply chatGroupApply,SohoUserDetails sohoUserDetails) {
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
    }

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

        return R.success(doGroupAddUser(chatGroupApply, sohoUserDetails.getId()));
    }

    /**
     * 执行添加用户到群
     *
     * @param chatGroupApply
     * @param adminUid
     * @return
     */
    private Boolean doGroupAddUser(ChatGroupApply chatGroupApply, Long adminUid) {
        if(adminUid != null && adminUid>0) {
            //检查用户是否为审批群的管理员
            LambdaQueryWrapper<ChatGroupUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ChatGroupUser::getGroupId, chatGroupApply.getGroupId());
            lambdaQueryWrapper.eq(ChatGroupUser::getChatUid, adminUid);
            lambdaQueryWrapper.eq(ChatGroupUser::getIsAdmin, ChatGroupUserEnums.IsAdmin.YES.getId());
            ChatGroupUser chatGroupUser = chatGroupUserService.getOne(lambdaQueryWrapper);
            Assert.notNull(chatGroupUser, "非管理员不能操作");
        }

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

        //申请人用户信息
        ChatUser chatUser = chatUserService.getById(chatGroupApply.getChatUid());

        //加入session user
        LambdaQueryWrapper<ChatSessionUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatSessionUser::getSessionId, chatSession.getId())
                .eq(ChatSessionUser::getUserId, chatGroupApply.getChatUid());
        ChatSessionUser chatSessionUser = chatSessionUserService.getOne(lambdaQueryWrapper);
        if(chatSessionUser == null) {
            chatSessionUser = new ChatSessionUser();
            chatSessionUser.setUserId(chatGroupApply.getChatUid());
            chatSessionUser.setSessionId(chatSession.getId());
            chatSessionUser.setUpdatedTime(LocalDateTime.now());
            chatSessionUser.setCreatedTime(LocalDateTime.now());
            chatSessionUser.setStatus(ChatSessionUserEnums.Status.ACTIVE.getId());
            chatSessionUserService.save(chatSessionUser);
        } else {
            chatSessionUser.setUpdatedTime(LocalDateTime.now());
            chatSessionUser.setStatus(ChatSessionUserEnums.Status.ACTIVE.getId());
            chatSessionUserService.updateById(chatSessionUser);
        }

        //更新会话统计信息
        chatSessionService.syncInfo(chatSession.getId());

        //发送系统通知
        chatService.chat(new ChatMessage.ChatMessageBuilder<SystemMessage>(chatSession.getId(), new SystemMessage.Builder().text(chatUser.getUsername() +" 加入群聊").build()).build());
        return true;
    }
}
