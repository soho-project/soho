package work.soho.chat.biz.service;

import work.soho.chat.api.payload.ChatMessage;
import work.soho.chat.biz.domain.ChatSession;
import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.enums.ChatSessionEnums;

import java.util.List;

public interface ChatSessionService extends IService<ChatSession> {
    /**
     * 根据条件查询会话
     *
     * @param uid
     * @param toUid
     * @return
     */
    ChatSession findCustomerServiceSession(Long uid, Long toUid);

    /**
     * 查找好友会话
     *
     * @param uid
     * @param toUid
     * @return
     */
    ChatSession findFriendSession(Long uid, Long toUid);

    /**
     * 创建会话
     *
     * @param uid
     * @param uids
     * @param type
     * @return
     */
    ChatSession createSession(Long uid, List<Long> uids, ChatSessionEnums.Type type);

    /**
     * 查找会话中的客服用户
     *
     * @param sessionId
     * @return
     */
    ChatSessionUser findCustomerService(Long sessionId);

    /**
     * 获取指定会话的用户
     *
     * @param sessionId
     * @return
     */
    List<ChatSessionUser> getSessionUser(String sessionId);

    /**
     * 聊天消息分发
     *
     * @param inputChatMessage
     */
    void chat(ChatMessage inputChatMessage);
}
