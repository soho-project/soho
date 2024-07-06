package work.soho.chat.biz.listen;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import work.soho.chat.api.ChatMessage;
import work.soho.chat.biz.service.ChatAiService;
import work.soho.chat.biz.service.ChatService;
import work.soho.chat.biz.utils.MessageUtils;
import work.soho.longlink.api.event.MessageEvent;
import work.soho.longlink.api.sender.Sender;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class LongLinkListen {
    private final Sender sender;

    private final ChatAiService chatAiService;

    private final ChatService chatService;

    @EventListener
    public void onMessage(MessageEvent messageEvent) {
        try {
            log.info("监听器收到消息: {}", messageEvent);
            ChatMessage upMessage = MessageUtils.fromMessageEvent(messageEvent);
            // 检查消息是否为有效消息
            if(upMessage == null) {
                //ignore  非本模块消息，或者非法消息
                return;
            }

            log.info("解析后的消息：", upMessage);
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
