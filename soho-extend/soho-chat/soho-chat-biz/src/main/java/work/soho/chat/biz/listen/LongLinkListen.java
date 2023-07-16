package work.soho.chat.biz.listen;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import work.soho.chat.api.ChatMessage;
import work.soho.chat.api.payload.Text;
import work.soho.common.core.util.JacksonUtils;
import work.soho.longlink.api.event.MessageEvent;
import work.soho.longlink.api.sender.Sender;

import java.util.Date;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class LongLinkListen {
    private final Sender sender;

    @EventListener
    public void onMessage(MessageEvent messageEvent) {
        log.info("监听器收到消息: {}", messageEvent);
        Text text = new Text();
        Text.Content content = new Text.Content();
        content.setText("Date Time: " + (new Date()).toString());
        text.setContent(content);
        Text.User user = new Text.User();
        text.setUser(user);

        ChatMessage<Text> chatMessage = new ChatMessage<>();
        chatMessage.setMessage(text);
        chatMessage.setFromUid(messageEvent.getUid());
        chatMessage.setToSessionId(messageEvent.getUid());

        sender.sendToUid("1", JacksonUtils.toJson(chatMessage));
    }
}
