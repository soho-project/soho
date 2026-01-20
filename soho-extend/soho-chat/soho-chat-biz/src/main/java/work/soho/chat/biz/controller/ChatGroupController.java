package work.soho.chat.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.chat.biz.domain.ChatGroup;
import work.soho.chat.biz.service.ChatGroupService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;

import java.util.Arrays;
import java.util.List;
/**
 * 群组Controller
 *
 * @author fang
 */
@Api(tags = "聊天群组")
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat/admin/chatGroup" )
public class ChatGroupController {

    private final ChatGroupService chatGroupService;

    /**
     * 查询群组列表
     */
    @GetMapping("/list")
    @Node(value = "chatGroup::list", name = "群组列表")
    public R<PageSerializable<ChatGroup>> list(ChatGroup chatGroup, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatGroup> lqw = new LambdaQueryWrapper<ChatGroup>();
        lqw.eq(chatGroup.getId() != null, ChatGroup::getId ,chatGroup.getId());
        lqw.like(StringUtils.isNotBlank(chatGroup.getTitle()),ChatGroup::getTitle ,chatGroup.getTitle());
        lqw.eq(chatGroup.getType() != null, ChatGroup::getType ,chatGroup.getType());
        lqw.eq(chatGroup.getMasterChatUid() != null, ChatGroup::getMasterChatUid ,chatGroup.getMasterChatUid());
        lqw.like(StringUtils.isNotBlank(chatGroup.getAvatar()),ChatGroup::getAvatar ,chatGroup.getAvatar());
        lqw.like(StringUtils.isNotBlank(chatGroup.getIntroduction()),ChatGroup::getIntroduction ,chatGroup.getIntroduction());
        lqw.like(StringUtils.isNotBlank(chatGroup.getProclamation()),ChatGroup::getProclamation ,chatGroup.getProclamation());
        lqw.eq(chatGroup.getUpdatedTime() != null, ChatGroup::getUpdatedTime ,chatGroup.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ChatGroup::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ChatGroup::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ChatGroup> list = chatGroupService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取群组详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "chatGroup::getInfo", name = "群组详细信息")
    public R<ChatGroup> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatGroupService.getById(id));
    }

    /**
     * 新增群组
     */
    @PostMapping
    @Node(value = "chatGroup::add", name = "群组新增")
    public R<Boolean> add(@RequestBody ChatGroup chatGroup) {
        return R.success(chatGroupService.save(chatGroup));
    }

    /**
     * 修改群组
     */
    @PutMapping
    @Node(value = "chatGroup::edit", name = "群组修改")
    public R<Boolean> edit(@RequestBody ChatGroup chatGroup) {
        return R.success(chatGroupService.updateById(chatGroup));
    }

    /**
     * 删除群组
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "chatGroup::remove", name = "群组删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(chatGroupService.removeByIds(Arrays.asList(ids)));
    }
}