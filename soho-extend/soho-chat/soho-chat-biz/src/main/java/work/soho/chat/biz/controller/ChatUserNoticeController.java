package work.soho.chat.biz.controller;

import java.time.LocalDateTime;
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
import work.soho.common.core.util.StringUtils;
import com.github.pagehelper.PageSerializable;
import work.soho.common.core.result.R;
import work.soho.api.admin.annotation.Node;
import work.soho.chat.biz.domain.ChatUserNotice;
import work.soho.chat.biz.service.ChatUserNoticeService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.api.admin.vo.TreeNodeVo;
/**
 * 聊天通知Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/chatUserNotice" )
public class ChatUserNoticeController {

    private final ChatUserNoticeService chatUserNoticeService;

    /**
     * 查询聊天通知列表
     */
    @GetMapping("/list")
    @Node(value = "chatUserNotice::list", name = "聊天通知列表")
    public R<PageSerializable<ChatUserNotice>> list(ChatUserNotice chatUserNotice, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatUserNotice> lqw = new LambdaQueryWrapper<ChatUserNotice>();
        lqw.eq(chatUserNotice.getId() != null, ChatUserNotice::getId ,chatUserNotice.getId());
        lqw.eq(chatUserNotice.getChatUid() != null, ChatUserNotice::getChatUid ,chatUserNotice.getChatUid());
        lqw.eq(chatUserNotice.getStatus() != null, ChatUserNotice::getStatus ,chatUserNotice.getStatus());
        lqw.eq(chatUserNotice.getType() != null, ChatUserNotice::getType ,chatUserNotice.getType());
        lqw.eq(chatUserNotice.getTrackingId() != null, ChatUserNotice::getTrackingId ,chatUserNotice.getTrackingId());
        lqw.eq(chatUserNotice.getUpdatedTime() != null, ChatUserNotice::getUpdatedTime ,chatUserNotice.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, ChatUserNotice::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, ChatUserNotice::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        List<ChatUserNotice> list = chatUserNoticeService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取聊天通知详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "chatUserNotice::getInfo", name = "聊天通知详细信息")
    public R<ChatUserNotice> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatUserNoticeService.getById(id));
    }

    /**
     * 新增聊天通知
     */
    @PostMapping
    @Node(value = "chatUserNotice::add", name = "聊天通知新增")
    public R<Boolean> add(@RequestBody ChatUserNotice chatUserNotice) {
        return R.success(chatUserNoticeService.save(chatUserNotice));
    }

    /**
     * 修改聊天通知
     */
    @PutMapping
    @Node(value = "chatUserNotice::edit", name = "聊天通知修改")
    public R<Boolean> edit(@RequestBody ChatUserNotice chatUserNotice) {
        return R.success(chatUserNoticeService.updateById(chatUserNotice));
    }

    /**
     * 删除聊天通知
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "chatUserNotice::remove", name = "聊天通知删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(chatUserNoticeService.removeByIds(Arrays.asList(ids)));
    }
}