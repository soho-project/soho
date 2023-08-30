package work.soho.chat.biz.service;

import work.soho.chat.biz.domain.ChatUserNotice;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ChatUserNoticeService extends IService<ChatUserNotice> {
    ChatUserNotice createGroupApplyNotice(Long chatUid, Long trackingId);
}
