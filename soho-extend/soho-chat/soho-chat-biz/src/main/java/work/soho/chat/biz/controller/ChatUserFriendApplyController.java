package work.soho.chat.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.chat.biz.domain.ChatUserFriendApply;
import work.soho.chat.biz.service.ChatUserFriendApplyService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;

import java.util.Arrays;
import java.util.List;
/**
 * 好友申请Controller
 *
 * @author fang
 */
@Api(tags = "好友申请管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat/admin/chatUserFriendApply" )
public class ChatUserFriendApplyController {

    private final ChatUserFriendApplyService chatUserFriendApplyService;

    /**
     * 查询好友申请列表
     */
    @GetMapping("/list")
    @Node(value = "chatUserFriendApply::list", name = "好友申请列表")
    public R<PageSerializable<ChatUserFriendApply>> list(ChatUserFriendApply chatUserFriendApply, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatUserFriendApply> lqw = new LambdaQueryWrapper<ChatUserFriendApply>();
        lqw.eq(chatUserFriendApply.getId() != null, ChatUserFriendApply::getId ,chatUserFriendApply.getId());
        lqw.eq(chatUserFriendApply.getChatUid() != null, ChatUserFriendApply::getChatUid ,chatUserFriendApply.getChatUid());
        lqw.eq(chatUserFriendApply.getFriendUid() != null, ChatUserFriendApply::getFriendUid ,chatUserFriendApply.getFriendUid());
        lqw.eq(chatUserFriendApply.getStatus() != null, ChatUserFriendApply::getStatus ,chatUserFriendApply.getStatus());
        lqw.like(StringUtils.isNotBlank(chatUserFriendApply.getAsk()),ChatUserFriendApply::getAsk ,chatUserFriendApply.getAsk());
        lqw.like(StringUtils.isNotBlank(chatUserFriendApply.getAnswer()),ChatUserFriendApply::getAnswer ,chatUserFriendApply.getAnswer());
        lqw.eq(chatUserFriendApply.getUpdatedTime() != null, ChatUserFriendApply::getUpdatedTime ,chatUserFriendApply.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ChatUserFriendApply::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ChatUserFriendApply::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ChatUserFriendApply> list = chatUserFriendApplyService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取好友申请详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "chatUserFriendApply::getInfo", name = "好友申请详细信息")
    public R<ChatUserFriendApply> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatUserFriendApplyService.getById(id));
    }

    /**
     * 新增好友申请
     */
    @PostMapping
    @Node(value = "chatUserFriendApply::add", name = "好友申请新增")
    public R<Boolean> add(@RequestBody ChatUserFriendApply chatUserFriendApply) {
        return R.success(chatUserFriendApplyService.save(chatUserFriendApply));
    }

    /**
     * 修改好友申请
     */
    @PutMapping
    @Node(value = "chatUserFriendApply::edit", name = "好友申请修改")
    public R<Boolean> edit(@RequestBody ChatUserFriendApply chatUserFriendApply) {
        return R.success(chatUserFriendApplyService.updateById(chatUserFriendApply));
    }

    /**
     * 删除好友申请
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "chatUserFriendApply::remove", name = "好友申请删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(chatUserFriendApplyService.removeByIds(Arrays.asList(ids)));
    }
}