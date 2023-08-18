package work.soho.chat.biz.listen;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import work.soho.chat.api.payload.ChatMessage;
import work.soho.chat.api.payload.Text;
import work.soho.chat.biz.service.ChatAiService;
import work.soho.chat.biz.service.ChatService;
import work.soho.chat.biz.service.ChatSessionService;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.JacksonUtils;
import work.soho.longlink.api.event.MessageEvent;
import work.soho.longlink.api.sender.Sender;

import java.util.Date;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class LongLinkListen {
    private final Sender sender;

    private final ChatAiService chatAiService;

//    private final ChatSessionService chatSessionService;

    private final ChatService chatService;

    @EventListener
    public void onMessage(MessageEvent messageEvent) {
        try {
            log.info("监听器收到消息: {}", messageEvent);
            Text text = new Text();
            Text.Content content = new Text.Content();
            content.setText("Date Time: " + (new Date()).toString());
            text.setContent(content);
            Text.User user = new Text.User();
            text.setUser(user);

            System.out.println(messageEvent.getPayload());
            log.info("payload: {}", messageEvent.getPayload());
            ChatMessage upMessage = JacksonUtils.toBean(messageEvent.getPayload(), new TypeReference<ChatMessage<Text>>() {});
            upMessage.setFromUid(messageEvent.getUid());
//            Text upMessage = JacksonUtils.toBean(messageEvent.getPayload(), Text.class);
            log.info("up message: {}", upMessage);

            //TODO 分发会话消息
            chatService.chat(upMessage);

//        // 获取AI答案
//            String responseText = chatAiService.chat(upMessage.getContent().getText());
//            String responseText = chatAiService.chat(((Text)upMessage.getMessage()).getContent().getText());
////
////        String responseText = IDGeneratorUtils.snowflake().toString();
//
//            Text t = new Text();
//            Text.Content c = new Text.Content();
//            c.setText(responseText);
//            t.setContent(c);
//            t.setId(((Text)upMessage.getMessage()).getId());
//            ChatMessage<Text> chatMessage = new ChatMessage<>();
//            chatMessage.setMessage(t);
//            chatMessage.setFromUid(messageEvent.getUid());
//            chatMessage.setToSessionId(upMessage.getToSessionId());
//
//            sender.sendToUid(messageEvent.getUid(), JacksonUtils.toJson(chatMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
