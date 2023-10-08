package work.soho.chat.biz.controller;

import java.time.LocalDateTime;

import work.soho.chat.api.payload.ChatMessage;
import work.soho.chat.api.payload.SystemMessage;
import work.soho.chat.biz.req.SendMessageReq;
import work.soho.chat.biz.service.ChatService;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.api.admin.annotation.Node;
import work.soho.chat.biz.domain.ChatSession;
import work.soho.chat.biz.service.ChatSessionService;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;

/**
 * 聊天会话Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/chatSession" )
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    private final ChatService chatService;

    /**
     * 查询聊天会话列表
     */
    @GetMapping("/list")
    @Node(value = "chatSession::list", name = "聊天会话列表")
    public R<PageSerializable<ChatSession>> list(ChatSession chatSession, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatSession> lqw = new LambdaQueryWrapper<ChatSession>();
        if (chatSession.getId() != null){
            lqw.eq(ChatSession::getId ,chatSession.getId());
        }
        if (chatSession.getType() != null){
            lqw.eq(ChatSession::getType ,chatSession.getType());
        }
        if (chatSession.getStatus() != null){
            lqw.eq(ChatSession::getStatus ,chatSession.getStatus());
        }
        if (chatSession.getTitle() != null){
            lqw.eq(ChatSession::getTitle ,chatSession.getTitle());
        }
        if (chatSession.getAvatar() != null){
            lqw.eq(ChatSession::getAvatar ,chatSession.getAvatar());
        }
        if (chatSession.getUpdatedTime() != null){
            lqw.eq(ChatSession::getUpdatedTime ,chatSession.getUpdatedTime());
        }
        if (chatSession.getCreatedTime() != null){
            lqw.eq(ChatSession::getCreatedTime ,chatSession.getCreatedTime());
        }

        if(betweenCreatedTimeRequest.getStartTime()!= null) {
            lqw.gt(ChatSession::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        }
        if(betweenCreatedTimeRequest.getEndTime()!= null) {
            lqw.le(ChatSession::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        }
        lqw.orderByDesc(ChatSession::getId);
        List<ChatSession> list = chatSessionService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取聊天会话详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "chatSession::getInfo", name = "聊天会话详细信息")
    public R<ChatSession> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatSessionService.getById(id));
    }

    /**
     * 新增聊天会话
     */
    @PostMapping
    @Node(value = "chatSession::add", name = "聊天会话新增")
    public R<Boolean> add(@RequestBody ChatSession chatSession) {
        chatSession.setCreatedTime(LocalDateTime.now());
        chatSession.setUpdatedTime(LocalDateTime.now());
        return R.success(chatSessionService.save(chatSession));
    }

    /**
     * 修改聊天会话
     */
    @PutMapping
    @Node(value = "chatSession::edit", name = "聊天会话修改")
    public R<Boolean> edit(@RequestBody ChatSession chatSession) {
        chatSession.setUpdatedTime(LocalDateTime.now());
        return R.success(chatSessionService.updateById(chatSession));
    }

    /**
     * 删除聊天会话
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "chatSession::remove", name = "聊天会话删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(chatSessionService.removeByIds(Arrays.asList(ids)));
    }

    @PostMapping("/sendMessage")
    public R<Boolean> sendMessage(@RequestBody SendMessageReq sendMessageReq) {
        if(sendMessageReq.getMsgType() == 1) {
            sendMessageReq.getSessionIds().forEach(sessionId -> {
                //TODO 发送消息
                ChatMessage<SystemMessage> chatMessage = new ChatMessage<>();
                chatMessage.setToSessionId(String.valueOf(sessionId));
                chatMessage.setFromUid(String.valueOf(0));
                SystemMessage system = new SystemMessage();
                system.setId(IDGeneratorUtils.snowflake().toString());
                system.setType("system");
                SystemMessage.Content content = new SystemMessage.Content();
                content.setText(sendMessageReq.getContent());
                system.setContent(content);
                chatMessage.setMessage(system);
                chatService.chat(chatMessage);
            });
        } else {
            sendMessageReq.getSessionIds().forEach(sessionId -> {
                //TODO 发送消息
            });
        }
        return R.success(true);
    }
}
