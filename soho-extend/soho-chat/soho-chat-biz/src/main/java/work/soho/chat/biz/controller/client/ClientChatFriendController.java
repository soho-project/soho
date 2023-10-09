package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.api.admin.annotation.Node;
import work.soho.chat.biz.domain.ChatSession;
import work.soho.chat.biz.domain.ChatSessionUser;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.domain.ChatUserFriend;
import work.soho.chat.biz.enums.ChatSessionEnums;
import work.soho.chat.biz.enums.ChatSessionUserEnums;
import work.soho.chat.biz.service.ChatSessionService;
import work.soho.chat.biz.service.ChatSessionUserService;
import work.soho.chat.biz.service.ChatUserFriendService;
import work.soho.chat.biz.service.ChatUserService;
import work.soho.chat.biz.vo.BaseUserVO;
import work.soho.chat.biz.vo.UserFriendVO;
import work.soho.chat.biz.vo.UserVO;
import work.soho.common.core.result.R;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.PageUtils;

import java.time.LocalDateTime;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat/chat/chatUserFriend")
@RequiredArgsConstructor
public class ClientChatFriendController {
    private final ChatUserFriendService chatUserFriendService;

    private final ChatUserService chatUserService;

    private final ChatSessionUserService chatSessionUserService;

    private final ChatSessionService chatSessionService;

    /**
     * 查询好友列表
     */
    @GetMapping("/list")
    public R<PageSerializable<UserFriendVO>> list(ChatUserFriend chatUserFriend, @AuthenticationPrincipal SohoUserDetails sohoUserDetails)
    {
        return R.success(new PageSerializable<>(chatUserFriendService.getListByUid(sohoUserDetails.getId())));
    }

    @GetMapping("/searchUser")
    public R<List<UserVO>> findFendUser(String keyword, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<ChatUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据id查找
        lambdaQueryWrapper.eq(ChatUser::getId, keyword);
        lambdaQueryWrapper.or().like(ChatUser::getUsername, "%" + keyword) //查找用户名
                .or().like(ChatUser::getNickname, "%" + keyword)
                .last("limit 100");
        List<ChatUser> list = chatUserService.list(lambdaQueryWrapper);
        List<UserVO> result = BeanUtils.copyList(list, UserVO.class);
        //TODO 检查用户是否为当前请求用户好友
        return R.success(result);
    }

    /**
     * 申请好友
     * @return
     */
    @GetMapping("/apply")
    public R<Boolean> apply(Long friendUid,@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        ChatUser chatUser = chatUserService.getById(friendUid);
        Assert.notNull(chatUser);
        //添加好友
        chatUserFriendService.applyFriend(sohoUserDetails.getId(), chatUser.getId());
        return R.success(Boolean.TRUE);
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

    /**
     * 删除指定好友关系
     *
     * @param sohoUserDetails
     * @return
     */
    @DeleteMapping("/{sessionId}")
    public R<Boolean> delete(@PathVariable Long sessionId, @AuthenticationPrincipal SohoUserDetails sohoUserDetails) {

        ChatSession chatSession = chatSessionService.getById(sessionId);
        Assert.notNull(chatSession, "会话不存在");
        Assert.isTrue(chatSession.getType().equals(ChatSessionEnums.Type.PRIVATE_CHAT.getId()), "非好友关系");

        List<ChatSessionUser> chatSessionUserList = chatSessionUserService.getSessionUserList(sessionId);
        Optional<ChatSessionUser> sessionUser = chatSessionUserList.stream().filter(item->item.getUserId().equals(sohoUserDetails.getId())).findFirst();
        if(!sessionUser.isPresent()) {
            throw new RuntimeException("数据异常");
        }
        Optional<ChatSessionUser> friend = chatSessionUserList.stream().filter(item->!item.getUserId().equals(sohoUserDetails.getId())).findFirst();
        Assert.isTrue(friend.isPresent(), "数据异常");
        ChatSessionUser chatSessionUser = friend.get();

        //删除好友关系；单边删除
        LambdaQueryWrapper<ChatUserFriend> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatUserFriend::getChatUid, sohoUserDetails.getId())
                .eq(ChatUserFriend::getFriendUid, chatSessionUser.getUserId());
        ChatUserFriend chatUserFriend = chatUserFriendService.getOne(lambdaQueryWrapper);
        Assert.notNull(chatUserFriend, "非好友关系");
        chatUserFriendService.removeById(chatUserFriend.getId());

        //删除对应的会话用户
        chatSessionUser.setStatus(ChatSessionUserEnums.Status.DELETED.getId());
        chatSessionUser.setUpdatedTime(LocalDateTime.now());
        chatSessionUserService.updateById(chatSessionUser);
        return R.success(true);
    }

    /**
     * 获取会话好友信息
     *
     * @param sessionId
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/friendUser/{sessionId}")
    public R<BaseUserVO> getFriendInfo(@PathVariable Long sessionId, @AuthenticationPrincipal SohoUserDetails sohoUserDetails)
    {
        ChatSession chatSession = chatSessionService.getById(sessionId);
        Assert.notNull(chatSession, "会话不存在");
        Assert.isTrue(chatSession.getType().equals(ChatSessionEnums.Type.PRIVATE_CHAT.getId()), "非好友关系");

        List<ChatSessionUser> chatSessionUserList = chatSessionUserService.getSessionUserList(sessionId);
        Optional<ChatSessionUser> sessionUser = chatSessionUserList.stream().filter(item->item.getUserId().equals(sohoUserDetails.getId())).findFirst();
        if(!sessionUser.isPresent()) {
            throw new RuntimeException("数据异常");
        }
        Optional<ChatSessionUser> friend = chatSessionUserList.stream().filter(item->!item.getUserId().equals(sohoUserDetails.getId())).findFirst();
        Assert.isTrue(friend.isPresent(), "数据异常");
        ChatSessionUser chatSessionUser = friend.get();

        ChatUser chatUser = chatUserService.getById(chatSessionUser.getUserId());
        BaseUserVO baseUserVO = BeanUtils.copy(chatUser, BaseUserVO.class);
        return R.success(baseUserVO);
    }
}
