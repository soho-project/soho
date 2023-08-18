package work.soho.chat.biz.controller.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.api.payload.ChatMessage;
import work.soho.chat.api.payload.Text;
import work.soho.chat.api.service.QuestionService;
import work.soho.chat.biz.domain.ChatSession;
import work.soho.chat.biz.domain.ChatSessionMessage;
import work.soho.chat.biz.domain.ChatSessionMessageUser;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.enums.ChatSessionEnums;
import work.soho.chat.biz.enums.ChatSessionMessageUserEnums;
import work.soho.chat.biz.service.*;
import work.soho.common.core.result.R;
import work.soho.common.core.util.IDGeneratorUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/chat/chat/question")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    private final ChatSessionService chatSessionService;
    private final ChatSessionUserService chatSessionUserService;
    private final ChatSessionMessageService chatSessionMessageService;
    private final ChatSessionMessageUserService chatSessionMessageUserService;

    private final ChatService chatService;

    @GetMapping
    public R get(String q,Long sessionId, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        log.info(sohoUserDetails);
        ChatSession chatSession = chatSessionService.getById(sessionId);
        Assert.notNull(chatSession, "会话不存在，请勿直接请求接口");
        Assert.isTrue(chatSession.getType() == ChatSessionEnums.Type.CUSTOMER_SERVICE.getId(), "该会话不是客服会话");

        ChatMessage<Text> chatMessage = new ChatMessage<>();
        chatMessage.setFromUid(String.valueOf(sohoUserDetails.getId()));
        chatMessage.setToSessionId(String.valueOf(sessionId));
        Text text = new Text();
        text.setId(IDGeneratorUtils.snowflake().toString());
        Text.Content content = new Text.Content();
        content.setText(q);
        text.setContent(content);
        chatMessage.setMessage(text);
        chatService.chat(chatMessage);

        //TODO 聊天接口进行处理
        //response
        String responseContent = questionService.ask(null, q);
        return R.success(responseContent);
    }
}
