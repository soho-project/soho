package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.api.admin.annotation.Node;
import work.soho.chat.biz.domain.ChatUserFriend;
import work.soho.chat.biz.service.ChatUserFriendService;
import work.soho.chat.biz.vo.UserFriendVO;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat/chat/chatUserFriend")
@RequiredArgsConstructor
public class ClientChatFriendController {
    private final ChatUserFriendService chatUserFriendService;

    /**
     * 查询好友列表
     */
    @GetMapping("/list")
    public R<PageSerializable<UserFriendVO>> list(ChatUserFriend chatUserFriend, @AuthenticationPrincipal SohoUserDetails sohoUserDetails)
    {
        return R.success(new PageSerializable<>(chatUserFriendService.getListByUid(sohoUserDetails.getId())));
    }

    @PutMapping()
    public R<Boolean> update(@AuthenticationPrincipal SohoUserDetails sohoUserDetails, @RequestBody ChatUserFriend chatUserFriend) {
        //检查权限
        ChatUserFriend chatUserFriendOld = chatUserFriendService.getById(chatUserFriend.getId());
        Assert.notNull(chatUserFriendOld, "数据不存在");
        Assert.isNull(chatUserFriend.getFriendUid(), "非法访问");
        Assert.equals(sohoUserDetails.getId(), chatUserFriendOld.getChatUid(), "非法访问");
        //TODO 备注合法性检查
        chatUserFriendService.updateById(chatUserFriend);
        return R.success();
    }
}
