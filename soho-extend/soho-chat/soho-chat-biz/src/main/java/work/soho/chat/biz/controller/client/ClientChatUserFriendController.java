package work.soho.chat.biz.controller.client;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import work.soho.common.security.userdetails.SohoUserDetails;
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
import work.soho.longlink.api.sender.QueryLongLink;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Api(tags = "客户端好友关系")
@RestController
@RequestMapping("/chat/chat/chatUserFriend")
@RequiredArgsConstructor
public class ClientChatUserFriendController {
    private final ChatUserFriendService chatUserFriendService;

    private final ChatUserService chatUserService;

    private final ChatSessionUserService chatSessionUserService;

    private final ChatSessionService chatSessionService;

    private final QueryLongLink queryLongLink;

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


        //更新备注信息
        if(chatUserFriend.getNotesName() != null) {
            //同步信息到会话
            ChatSession chatSession = chatSessionService.findFriendSession(sohoUserDetails.getId(), chatUserFriendOld.getFriendUid());
            if(chatSession != null) {
                ChatSessionUser chatSessionUser = chatSessionUserService.getSessionUser(chatSession.getId(), sohoUserDetails.getId());
                if(chatSessionUser != null) {
                    //检查更新
                    chatSessionUser.setTitle(chatUserFriend.getNotesName());
                    chatSessionUser.setUpdatedTime(LocalDateTime.now());
                    chatSessionUserService.updateById(chatSessionUser);
                }
            }
        }

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

        //删除好友关系
        Optional<ChatSessionUser> friend = chatSessionUserList.stream().filter(item->!item.getUserId().equals(sohoUserDetails.getId())).findFirst();
        if(friend.isPresent()) {
            ChatSessionUser friendChatSessionUser = friend.get();
            //删除好友关系；单边删除
            LambdaQueryWrapper<ChatUserFriend> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ChatUserFriend::getChatUid, sohoUserDetails.getId())
                    .eq(ChatUserFriend::getFriendUid, friendChatSessionUser.getUserId());
            ChatUserFriend chatUserFriend = chatUserFriendService.getOne(lambdaQueryWrapper);
            Assert.notNull(chatUserFriend, "非好友关系");
            chatUserFriendService.removeById(chatUserFriend.getId());
        }

        //删除我的会话消息
        Optional<ChatSessionUser> myUser = chatSessionUserList.stream().filter(item->item.getUserId().equals(sohoUserDetails.getId())).findFirst();
        if(myUser.isPresent()) {
            ChatSessionUser myChatSessionUser = myUser.get();
            //删除对应的会话用户
            myChatSessionUser.setStatus(ChatSessionUserEnums.Status.DELETED.getId());
            myChatSessionUser.setUpdatedTime(LocalDateTime.now());
            chatSessionUserService.updateById(myChatSessionUser);
        }

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

    /**
     * 获取好友用户在线状态
     *
     * @param sohoUserDetails
     * @return
     */
    @GetMapping("/getFriendUsersStatus")
    public R<Map<String,Integer>> getFriendUsersStatus(@AuthenticationPrincipal SohoUserDetails sohoUserDetails) {
        LambdaQueryWrapper<ChatUserFriend> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatUserFriend::getChatUid, sohoUserDetails.getId());
        List<ChatUserFriend> friends = chatUserFriendService.list(lambdaQueryWrapper);
        List<String> uids = friends.stream().map(item->String.valueOf(item.getFriendUid())).collect(Collectors.toList());
        uids.add(String.valueOf(sohoUserDetails.getId()));
        return R.success(queryLongLink.getOnlineStatus(uids));
    }
}
