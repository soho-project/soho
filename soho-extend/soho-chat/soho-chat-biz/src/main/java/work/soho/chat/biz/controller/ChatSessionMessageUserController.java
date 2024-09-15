package work.soho.chat.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.annotation.Node;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.chat.biz.domain.ChatSessionMessageUser;
import work.soho.chat.biz.service.ChatSessionMessageUserService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;

import java.util.Arrays;
import java.util.List;
/**
 * Controller
 *
 * @author fang
 */
@Api(tags = "聊天会话用户消息")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/chatSessionMessageUser" )
public class ChatSessionMessageUserController {

    private final ChatSessionMessageUserService chatSessionMessageUserService;

    /**
     * 查询列表
     */
    @GetMapping("/list")
    @Node(value = "chatSessionMessageUser::list", name = "chat_session_message_user列表")
    public R<PageSerializable<ChatSessionMessageUser>> list(ChatSessionMessageUser chatSessionMessageUser, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatSessionMessageUser> lqw = new LambdaQueryWrapper<ChatSessionMessageUser>();
        lqw.eq(chatSessionMessageUser.getId() != null, ChatSessionMessageUser::getId ,chatSessionMessageUser.getId());
        lqw.eq(chatSessionMessageUser.getMessageId() != null, ChatSessionMessageUser::getMessageId ,chatSessionMessageUser.getMessageId());
        lqw.eq(chatSessionMessageUser.getUid() != null, ChatSessionMessageUser::getUid ,chatSessionMessageUser.getUid());
        lqw.eq(chatSessionMessageUser.getIsRead() != null, ChatSessionMessageUser::getIsRead ,chatSessionMessageUser.getIsRead());
        lqw.eq(chatSessionMessageUser.getUpdatedTime() != null, ChatSessionMessageUser::getUpdatedTime ,chatSessionMessageUser.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ChatSessionMessageUser::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ChatSessionMessageUser::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.orderByDesc(ChatSessionMessageUser::getId);
        List<ChatSessionMessageUser> list = chatSessionMessageUserService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "chatSessionMessageUser::getInfo", name = "chat_session_message_user详细信息")
    public R<ChatSessionMessageUser> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatSessionMessageUserService.getById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @Node(value = "chatSessionMessageUser::add", name = "chat_session_message_user新增")
    public R<Boolean> add(@RequestBody ChatSessionMessageUser chatSessionMessageUser) {
        return R.success(chatSessionMessageUserService.save(chatSessionMessageUser));
    }

    /**
     * 修改
     */
    @PutMapping
    @Node(value = "chatSessionMessageUser::edit", name = "chat_session_message_user修改")
    public R<Boolean> edit(@RequestBody ChatSessionMessageUser chatSessionMessageUser) {
        return R.success(chatSessionMessageUserService.updateById(chatSessionMessageUser));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "chatSessionMessageUser::remove", name = "chat_session_message_user删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(chatSessionMessageUserService.removeByIds(Arrays.asList(ids)));
    }
}
