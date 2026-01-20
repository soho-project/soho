package work.soho.chat.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.chat.biz.domain.ChatGroupUser;
import work.soho.chat.biz.service.ChatGroupUserService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;

import java.util.Arrays;
import java.util.List;
/**
 * 群组用户Controller
 *
 * @author fang
 */
@Api(tags = "聊天群组用户")
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat/admin/chatGroupUser" )
public class ChatGroupUserController {

    private final ChatGroupUserService chatGroupUserService;

    /**
     * 查询群组用户列表
     */
    @GetMapping("/list")
    @Node(value = "chatGroupUser::list", name = "群组用户列表")
    public R<PageSerializable<ChatGroupUser>> list(ChatGroupUser chatGroupUser, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatGroupUser> lqw = new LambdaQueryWrapper<ChatGroupUser>();
        lqw.eq(chatGroupUser.getId() != null, ChatGroupUser::getId ,chatGroupUser.getId());
        lqw.eq(chatGroupUser.getGroupId() != null, ChatGroupUser::getGroupId ,chatGroupUser.getGroupId());
        lqw.eq(chatGroupUser.getChatUid() != null, ChatGroupUser::getChatUid ,chatGroupUser.getChatUid());
        lqw.eq(chatGroupUser.getIsAdmin() != null, ChatGroupUser::getIsAdmin ,chatGroupUser.getIsAdmin());
        lqw.like(StringUtils.isNotBlank(chatGroupUser.getNotesName()),ChatGroupUser::getNotesName ,chatGroupUser.getNotesName());
        lqw.eq(chatGroupUser.getUpdatedTime() != null, ChatGroupUser::getUpdatedTime ,chatGroupUser.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ChatGroupUser::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ChatGroupUser::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ChatGroupUser> list = chatGroupUserService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取群组用户详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "chatGroupUser::getInfo", name = "群组用户详细信息")
    public R<ChatGroupUser> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatGroupUserService.getById(id));
    }

    /**
     * 新增群组用户
     */
    @PostMapping
    @Node(value = "chatGroupUser::add", name = "群组用户新增")
    public R<Boolean> add(@RequestBody ChatGroupUser chatGroupUser) {
        return R.success(chatGroupUserService.save(chatGroupUser));
    }

    /**
     * 修改群组用户
     */
    @PutMapping
    @Node(value = "chatGroupUser::edit", name = "群组用户修改")
    public R<Boolean> edit(@RequestBody ChatGroupUser chatGroupUser) {
        return R.success(chatGroupUserService.updateById(chatGroupUser));
    }

    /**
     * 删除群组用户
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "chatGroupUser::remove", name = "群组用户删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(chatGroupUserService.removeByIds(Arrays.asList(ids)));
    }
}