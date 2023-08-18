package work.soho.chat.biz.service;

import work.soho.chat.api.payload.ChatMessage;

public interface ChatService {
    void chat(ChatMessage inputChatMessage);
}
