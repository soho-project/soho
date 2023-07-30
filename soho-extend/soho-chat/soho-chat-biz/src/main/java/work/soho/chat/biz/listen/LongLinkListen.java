package work.soho.chat.biz.listen;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import work.soho.chat.api.payload.ChatMessage;
import work.soho.chat.api.payload.Text;
import work.soho.chat.biz.service.ChatAiService;
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
            Text upMessage = JacksonUtils.toBean(messageEvent.getPayload(), Text.class);
            log.info("up message: {}", upMessage);
//        // 获取AI答案
            String responseText = chatAiService.chat(upMessage.getContent().getText());

//        String responseText = IDGeneratorUtils.snowflake().toString();

            Text t = new Text();
            Text.Content c = new Text.Content();
            c.setText(responseText);
            t.setContent(c);
            ChatMessage<Text> chatMessage = new ChatMessage<>();
            chatMessage.setMessage(t);
            chatMessage.setFromUid(messageEvent.getUid());
            chatMessage.setToSessionId(messageEvent.getUid());

            sender.sendToUid(messageEvent.getUid(), JacksonUtils.toJson(chatMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
