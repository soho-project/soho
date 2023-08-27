package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatUserFriendApply;
import work.soho.chat.biz.domain.ChatUserNotice;
import work.soho.chat.biz.enums.ChatUserNoticeEnums;
import work.soho.chat.biz.mapper.ChatUserFriendApplyMapper;
import work.soho.chat.biz.mapper.ChatUserNoticeMapper;
import work.soho.chat.biz.service.ChatUserFriendApplyService;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ChatUserFriendApplyServiceImpl extends ServiceImpl<ChatUserFriendApplyMapper, ChatUserFriendApply>
    implements ChatUserFriendApplyService{

    private final ChatUserNoticeMapper chatUserNoticeMapper;

    /**
     * 创建好友申请单
     *
     * @param chatUserFriendApply
     * @return
     */
    @Override
    public Boolean createApply(ChatUserFriendApply chatUserFriendApply) {
        //TODO 重复请求检查，短期内禁止重复创建

        chatUserFriendApply.setCreatedTime(LocalDateTime.now());
        chatUserFriendApply.setUpdatedTime(LocalDateTime.now());
        save(chatUserFriendApply);
        //源发送用户消息
        ChatUserNotice chatUserNotice = new ChatUserNotice();
        chatUserNotice.setChatUid(chatUserFriendApply.getChatUid());
        chatUserNotice.setType(ChatUserNoticeEnums.Type.FRIEND.getType());
        chatUserNotice.setStatus(ChatUserNoticeEnums.Status.PROCESSED.getId());
        chatUserNotice.setTrackingId(chatUserFriendApply.getId());
        chatUserNotice.setUpdatedTime(LocalDateTime.now());
        chatUserNotice.setCreatedTime(LocalDateTime.now());
        chatUserNoticeMapper.insert(chatUserNotice);
        //目标用户消息
        ChatUserNotice targetChatUserNotice = new ChatUserNotice();
        targetChatUserNotice.setChatUid(chatUserFriendApply.getFriendUid());
        targetChatUserNotice.setType(ChatUserNoticeEnums.Type.FRIEND.getType());
        targetChatUserNotice.setStatus(ChatUserNoticeEnums.Status.PENDING_PROCESSING.getId());
        targetChatUserNotice.setTrackingId(chatUserFriendApply.getId());
        targetChatUserNotice.setUpdatedTime(LocalDateTime.now());
        targetChatUserNotice.setCreatedTime(LocalDateTime.now());
        chatUserNoticeMapper.insert(targetChatUserNotice);

        //TODO 发送通知到客户端

        return Boolean.TRUE;
    }
}
