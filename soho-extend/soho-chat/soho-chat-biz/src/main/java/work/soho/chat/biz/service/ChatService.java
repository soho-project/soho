package work.soho.chat.biz.service;

import work.soho.chat.api.ChatMessage;
import work.soho.chat.biz.domain.ChatSession;
import work.soho.chat.biz.domain.ChatSessionUser;

import java.util.List;

public interface ChatService {
    /**
     * 处理聊天会话信息
     *
     * 转发消息；进行消息持久化
     *
     * @param inputChatMessage
     */
    void chat(ChatMessage inputChatMessage);

    /**
     * 发送消息给指定用户
     *
     * @param users
     * @param inputChatMessage
     */
    void send2Users(List<ChatSessionUser> users, ChatMessage inputChatMessage);

    /**
     * 发送消息给会话
     *
     * @param session
     * @param inputChatMessage
     */
    void send2Session(ChatSession session, ChatMessage inputChatMessage);

    /**
     * 发送消息给指定会话
     *
     * @param sessionId
     * @param inputChatMessage
     */
    void send2Session(Long sessionId, ChatMessage inputChatMessage);
}
