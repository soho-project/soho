package work.soho.chat.biz.service;

import work.soho.chat.biz.domain.ChatSessionMessageUser;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ChatSessionMessageUserService extends IService<ChatSessionMessageUser> {
    /**
     * 确认消息已读
     *
     * @param messageId
     * @param uid
     */
    void isRead(Long messageId, Long uid);
}
