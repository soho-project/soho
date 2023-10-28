package work.soho.chat.biz.service.impl;

import cn.hutool.core.lang.Assert;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.domain.ChatUserFriend;
import work.soho.chat.biz.mapper.ChatUserFriendMapper;
import work.soho.chat.biz.mapper.ChatUserMapper;
import work.soho.chat.biz.service.ChatUserFriendService;
import work.soho.chat.biz.vo.UserFriendVO;
import work.soho.common.core.util.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatUserFriendServiceImpl extends ServiceImpl<ChatUserFriendMapper, ChatUserFriend>
    implements ChatUserFriendService{

    /**
     * 聊天用户mapper
     */
    private final ChatUserMapper chatUserMapper;


    /**
     * 获取指定用户好友
     *
     * @param uid
     */
    public List<UserFriendVO> getListByUid(Long uid) {
        LambdaQueryWrapper<ChatUserFriend> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ChatUserFriend::getChatUid, uid);
        List<ChatUserFriend> list = list(lqw);

        List<Long> userIds = list.stream().map(ChatUserFriend::getFriendUid).collect(Collectors.toList());
        if(userIds.isEmpty()) {
            return new ArrayList<>();
        }

        //获取用户信息
        LambdaQueryWrapper<ChatUser> chatUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatUserLambdaQueryWrapper.in(ChatUser::getId, userIds);
        List<ChatUser> userList = chatUserMapper.selectList(chatUserLambdaQueryWrapper);
        Map<Long, ChatUser> userMap = userList.stream().collect(Collectors.toMap(ChatUser::getId, item->item));

        List<UserFriendVO> result = new ArrayList<>();
        list.forEach(row->{
            UserFriendVO userFriendVO = BeanUtils.copy(userMap.get(row.getFriendUid()), UserFriendVO.class);
            org.springframework.beans.BeanUtils.copyProperties(row, userFriendVO);
            result.add(userFriendVO);
        });

        return result;
    }

    /**
     * 申请好友
     *
     * TODO 实现申请过程；暂时直接添加为好友
     *
     * @param uid
     * @param friendId
     * @return
     */
    @Override
    public Boolean applyFriend(Long uid, Long friendId) {
        ChatUser chatUser = chatUserMapper.selectById(uid);
        ChatUser friendUser = chatUserMapper.selectById(friendId);
        Assert.notNull(chatUser);
        Assert.notNull(friendUser);

        //TODO 检查是否已经存在
        ChatUserFriend chatUserFriend = new ChatUserFriend();
        chatUserFriend.setChatUid(uid);
        chatUserFriend.setFriendUid(friendId);
        chatUserFriend.setNotesName(friendUser.getNickname());
        saveOrUpdate(chatUserFriend);

        //TODO 检查是否已经存在
        ChatUserFriend chatUserFriend1 = new ChatUserFriend();
        chatUserFriend1.setChatUid(friendId);
        chatUserFriend1.setFriendUid(uid);
        chatUserFriend1.setNotesName(chatUser.getNickname());
        saveOrUpdate(chatUserFriend1);

        return Boolean.TRUE;
    }

    @Override
    public void createFriend(ChatUserFriend chatUserFriend) {
        LambdaQueryWrapper<ChatUserFriend> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatUserFriend::getChatUid, chatUserFriend.getChatUid())
                .eq(ChatUserFriend::getFriendUid, chatUserFriend.getFriendUid());
        ChatUserFriend oldChatUserFriend = getOne(lambdaQueryWrapper);
        if(oldChatUserFriend == null) {
            save(chatUserFriend);
        }
    }
}
