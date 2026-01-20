package work.soho.chat.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.chat.biz.domain.ChatSessionMessage;
import work.soho.chat.biz.service.ChatSessionMessageService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;

import java.util.Arrays;
import java.util.List;
/**
 * Controller
 *
 * @author fang
 */
@Api(tags = "聊天会话ID")
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat/admin/chatSessionMessage" )
public class ChatSessionMessageController {

    private final ChatSessionMessageService chatSessionMessageService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "chatSessionMessage::list", name = "chat_session_message列表")
    public R<PageSerializable<ChatSessionMessage>> list(ChatSessionMessage chatSessionMessage, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatSessionMessage> lqw = new LambdaQueryWrapper<ChatSessionMessage>();
        lqw.eq(chatSessionMessage.getId() != null, ChatSessionMessage::getId ,chatSessionMessage.getId());
        lqw.eq(chatSessionMessage.getFromUid() != null, ChatSessionMessage::getFromUid ,chatSessionMessage.getFromUid());
        lqw.eq(chatSessionMessage.getSessionId() != null, ChatSessionMessage::getSessionId ,chatSessionMessage.getSessionId());
        lqw.like(StringUtils.isNotBlank(chatSessionMessage.getContent()),ChatSessionMessage::getContent ,chatSessionMessage.getContent());
        lqw.eq(chatSessionMessage.getUpdatedTime() != null, ChatSessionMessage::getUpdatedTime ,chatSessionMessage.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ChatSessionMessage::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ChatSessionMessage::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ChatSessionMessage::getId);
        List<ChatSessionMessage> list = chatSessionMessageService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "chatSessionMessage::getInfo", name = "chat_session_message详细信息")
    public R<ChatSessionMessage> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatSessionMessageService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "chatSessionMessage::add", name = "chat_session_message新增")
    public R<Boolean> add(@RequestBody ChatSessionMessage chatSessionMessage) {
        return R.success(chatSessionMessageService.save(chatSessionMessage));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "chatSessionMessage::edit", name = "chat_session_message修改")
    public R<Boolean> edit(@RequestBody ChatSessionMessage chatSessionMessage) {
        return R.success(chatSessionMessageService.updateById(chatSessionMessage));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "chatSessionMessage::remove", name = "chat_session_message删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(chatSessionMessageService.removeByIds(Arrays.asList(ids)));
    }
}
