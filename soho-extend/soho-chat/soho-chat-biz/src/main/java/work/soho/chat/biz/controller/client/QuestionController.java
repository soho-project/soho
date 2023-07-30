package work.soho.chat.biz.controller.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.api.service.QuestionService;
import work.soho.chat.biz.domain.ChatSession;
import work.soho.chat.biz.domain.ChatSessionMessage;
import work.soho.chat.biz.domain.ChatSessionMessageUser;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.enums.ChatSessionEnums;
import work.soho.chat.biz.enums.ChatSessionMessageUserEnums;
import work.soho.chat.biz.service.ChatSessionMessageService;
import work.soho.chat.biz.service.ChatSessionMessageUserService;
import work.soho.chat.biz.service.ChatSessionService;
import work.soho.chat.biz.service.ChatSessionUserService;
import work.soho.common.core.result.R;

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

    @GetMapping
    public R get(String q,Long sessionId, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        log.info(sohoUserDetails);
        ChatSession chatSession = chatSessionService.getById(sessionId);
        Assert.notNull(chatSession, "会话不存在，请勿直接请求接口");
        Assert.isTrue(chatSession.getType() == ChatSessionEnums.Type.CUSTOMER_SERVICE.getId(), "该会话不是客服会话");
        //new
        saveMessage(q, sohoUserDetails.getId(), chatSession.getId());
        //response
        String responseContent = questionService.ask(null, q);
        //获取客服用户ID
        Long customerServiceUserId = chatSessionService.findCustomerService(chatSession.getId()).getUserId();
        saveMessage(responseContent, customerServiceUserId, chatSession.getId());

        return R.success(responseContent);
    }

    /**
     * 保存消息
     *
     * @param content
     * @param fromUid
     * @param sessionId
     */
    private void saveMessage(String content, Long fromUid, Long sessionId) {
        ChatSessionMessage chatSessionMessage = chatSessionMessageService.dispatchingMessage(fromUid, sessionId, content);
        chatSessionUserService.getSessionUserList(sessionId).forEach(item -> {
            chatSessionMessageUserService.isRead(chatSessionMessage.getId(), item.getUserId());
        });
    }
}
