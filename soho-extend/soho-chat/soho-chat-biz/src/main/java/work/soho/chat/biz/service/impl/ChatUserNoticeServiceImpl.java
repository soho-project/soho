package work.soho.chat.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatUserNotice;
import work.soho.chat.biz.enums.ChatUserNoticeEnums;
import work.soho.chat.biz.mapper.ChatUserNoticeMapper;
import work.soho.chat.biz.service.ChatUserNoticeService;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ChatUserNoticeServiceImpl extends ServiceImpl<ChatUserNoticeMapper, ChatUserNotice>
    implements ChatUserNoticeService{

    /**
     * 创建群组通知
     *
     * @param chatUid
     * @param trackingId
     */
    @Override
    public ChatUserNotice createGroupApplyNotice(Long chatUid, Long trackingId) {
        ChatUserNotice chatUserNotice = new ChatUserNotice();
        chatUserNotice.setChatUid(chatUid);
        chatUserNotice.setTrackingId(trackingId);
        chatUserNotice.setType(ChatUserNoticeEnums.Type.GROUP.getType());
        chatUserNotice.setStatus(ChatUserNoticeEnums.Status.PENDING_PROCESSING.getId());
        chatUserNotice.setUpdatedTime(LocalDateTime.now());
        chatUserNotice.setCreatedTime(LocalDateTime.now());
        save(chatUserNotice);
        return chatUserNotice;
    }
}
