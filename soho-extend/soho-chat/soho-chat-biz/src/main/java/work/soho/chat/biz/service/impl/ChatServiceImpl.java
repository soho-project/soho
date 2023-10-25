package work.soho.chat.biz.service.impl;

import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import work.soho.chat.api.ChatMessage;
import work.soho.chat.api.payload.PayloadBaseInterface;
import work.soho.chat.biz.domain.ChatSession;
import work.soho.chat.biz.domain.ChatSessionMessage;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.service.*;
import work.soho.common.core.util.JacksonUtils;
import work.soho.longlink.api.sender.Sender;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatSessionService chatSessionService;

    private final ChatSessionMessageService chatSessionMessageService;

    private final ChatSessionUserService chatSessionUserService;

    private final ChatSessionMessageUserService chatSessionMessageUserService;

    /**
     * message sender api
     */
    private final Sender sender;

    /**
     * 聊天处理
     *
     * @param inputChatMessage
     */
    @Override
    public void chat(ChatMessage inputChatMessage) {
        List<ChatSessionUser> sessionUsers = chatSessionService.getSessionUser(inputChatMessage.getToSessionId());

        //存储会话消息
        saveMessage(inputChatMessage);
        //更新会话最后一条消息

        for(ChatSessionUser chatSessionUser: sessionUsers) {
            log.info("当前转发消息用户信息： {}", chatSessionUser);
            //检查是否为发送用户
            if(chatSessionUser.getUserId().equals(Long.valueOf(inputChatMessage.getFromUid()))) {
                //这是发送人
                //ignore
            } else {
                //推送消息到信道给到指定用户
                sender.sendToUid(String.valueOf(chatSessionUser.getUserId()), JacksonUtils.toJson(inputChatMessage));
            }
        }
        //更新会话最后消息时间
        ChatSession chatSession = chatSessionService.getById(inputChatMessage.getToSessionId());
        Assert.notNull(chatSession, "没有找到对应的会话");
        chatSession.setUpdatedTime(LocalDateTime.now());
        chatSession.setLastMessageTime(LocalDateTime.now());
        chatSession.setLastMessage(JacksonUtils.toJson(inputChatMessage));
        chatSessionService.updateById(chatSession);
    }

    /**
     * 存储会话消息
     *
     * @param inputChatMessage
     */
    private void saveMessage(ChatMessage inputChatMessage) {
        Long fromUid = Long.parseLong(inputChatMessage.getFromUid());
        Long sessionId = Long.parseLong(inputChatMessage.getToSessionId());
        String clientMessageId = ((PayloadBaseInterface)inputChatMessage.getMessage()).getId();
        ChatSessionMessage chatSessionMessage = chatSessionMessageService.dispatchingMessage(fromUid, sessionId, clientMessageId, JacksonUtils.toJson(inputChatMessage.getMessage()));
        chatSessionUserService.getSessionUserList(sessionId).forEach(item -> {
            chatSessionMessageUserService.isRead(chatSessionMessage.getId(), item.getUserId());
        });
    }
}
