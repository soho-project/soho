package work.soho.chat.biz.listen;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import work.soho.chat.api.payload.ChatMessage;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.service.ChatSessionUserService;
import work.soho.common.core.util.JacksonUtils;
import work.soho.longlink.api.sender.Sender;

import java.util.List;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class SendMessageListen {
    private final Sender sender;
    private final ChatSessionUserService chatSessionUserService;

    @EventListener
    public void systemMessage(ChatMessage chatMessage) {
        List<ChatSessionUser> list = chatSessionUserService.getSessionUserList(Long.valueOf(chatMessage.getToSessionId()));
        list.forEach(item->{
            sender.sendToUid(String.valueOf(item.getUserId()), JacksonUtils.toJson(chatMessage));
        });
    }
}
