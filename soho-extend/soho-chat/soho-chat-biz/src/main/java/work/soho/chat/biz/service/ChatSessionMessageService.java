package work.soho.chat.biz.service;

import work.soho.chat.biz.domain.ChatSessionMessage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ChatSessionMessageService extends IService<ChatSessionMessage> {
    /**
     * 持久化消息
     *
     * @param fromUid
     * @param sessionId
     * @param content
     */
    ChatSessionMessage dispatchingMessage(Long fromUid,Long sessionId,String content);

    /**
     * 删除指定用户会话的消息
     *
     * @param id
     * @param uid
     * @return
     */
    Boolean removeSessionMessageById(Long id, Long uid);
}
