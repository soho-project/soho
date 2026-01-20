package work.soho.chat.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.common.security.annotation.Node;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.chat.biz.domain.ChatUserEmote;
import work.soho.chat.biz.service.ChatUserEmoteService;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;

import java.util.Arrays;
import java.util.List;
/**
 * 用户表情Controller
 *
 * @author fang
 */
@Api(tags = "聊天用户表情")
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat/admin/chatUserEmote" )
public class ChatUserEmoteController {

    private final ChatUserEmoteService chatUserEmoteService;

    /**
     * 查询用户表情列表
     */
    @GetMapping("/list")
    @Node(value = "chatUserEmote::list", name = "用户表情列表")
    public R<PageSerializable<ChatUserEmote>> list(ChatUserEmote chatUserEmote, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatUserEmote> lqw = new LambdaQueryWrapper<ChatUserEmote>();
        lqw.eq(chatUserEmote.getId() != null, ChatUserEmote::getId ,chatUserEmote.getId());
        lqw.eq(chatUserEmote.getUid() != null, ChatUserEmote::getUid ,chatUserEmote.getUid());
        lqw.like(StringUtils.isNotBlank(chatUserEmote.getUrl()),ChatUserEmote::getUrl ,chatUserEmote.getUrl());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ChatUserEmote::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ChatUserEmote::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ChatUserEmote> list = chatUserEmoteService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取用户表情详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "chatUserEmote::getInfo", name = "用户表情详细信息")
    public R<ChatUserEmote> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatUserEmoteService.getById(id));
    }

    /**
     * 新增用户表情
     */
    @PostMapping
    @Node(value = "chatUserEmote::add", name = "用户表情新增")
    public R<Boolean> add(@RequestBody ChatUserEmote chatUserEmote) {
        return R.success(chatUserEmoteService.save(chatUserEmote));
    }

    /**
     * 修改用户表情
     */
    @PutMapping
    @Node(value = "chatUserEmote::edit", name = "用户表情修改")
    public R<Boolean> edit(@RequestBody ChatUserEmote chatUserEmote) {
        return R.success(chatUserEmoteService.updateById(chatUserEmote));
    }

    /**
     * 删除用户表情
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "chatUserEmote::remove", name = "用户表情删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(chatUserEmoteService.removeByIds(Arrays.asList(ids)));
    }
}