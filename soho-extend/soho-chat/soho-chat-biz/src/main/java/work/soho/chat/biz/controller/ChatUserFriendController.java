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
import work.soho.chat.biz.domain.ChatUserFriend;
import work.soho.chat.biz.service.ChatUserFriendService;
import java.util.ArrayList;
import java.util.HashMap;
import work.soho.api.admin.vo.OptionVo;
import work.soho.api.admin.request.BetweenCreatedTimeRequest;
import java.util.stream.Collectors;
import work.soho.api.admin.vo.TreeNodeVo;
/**
 * 好友Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/chatUserFriend" )
public class ChatUserFriendController {

    private final ChatUserFriendService chatUserFriendService;

    /**
     * 查询好友列表
     */
    @GetMapping("/list")
    @Node(value = "chatUserFriend::list", name = "好友;;列表")
    public R<PageSerializable<ChatUserFriend>> list(ChatUserFriend chatUserFriend)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<ChatUserFriend> lqw = new LambdaQueryWrapper<ChatUserFriend>();
        lqw.eq(chatUserFriend.getId() != null, ChatUserFriend::getId ,chatUserFriend.getId());
        lqw.eq(chatUserFriend.getChatUid() != null, ChatUserFriend::getChatUid ,chatUserFriend.getChatUid());
        lqw.eq(chatUserFriend.getFriendUid() != null, ChatUserFriend::getFriendUid ,chatUserFriend.getFriendUid());
        lqw.eq(chatUserFriend.getNotesName() != null, ChatUserFriend::getNotesName ,chatUserFriend.getNotesName());
        List<ChatUserFriend> list = chatUserFriendService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取好友详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "chatUserFriend::getInfo", name = "好友;;详细信息")
    public R<ChatUserFriend> getInfo(@PathVariable("id" ) Long id) {
        return R.success(chatUserFriendService.getById(id));
    }

    /**
     * 新增好友
     */
    @PostMapping
    @Node(value = "chatUserFriend::add", name = "好友;;新增")
    public R<Boolean> add(@RequestBody ChatUserFriend chatUserFriend) {
        return R.success(chatUserFriendService.save(chatUserFriend));
    }

    /**
     * 修改好友
     */
    @PutMapping
    @Node(value = "chatUserFriend::edit", name = "好友;;修改")
    public R<Boolean> edit(@RequestBody ChatUserFriend chatUserFriend) {
        return R.success(chatUserFriendService.updateById(chatUserFriend));
    }

    /**
     * 删除好友
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "chatUserFriend::remove", name = "好友;;删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(chatUserFriendService.removeByIds(Arrays.asList(ids)));
    }
}