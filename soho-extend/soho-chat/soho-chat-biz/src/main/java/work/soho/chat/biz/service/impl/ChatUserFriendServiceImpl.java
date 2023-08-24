package work.soho.chat.biz.service.impl;

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
        LambdaQueryWrapper<ChatUserFriend> lqw = new LambdaQueryWrapper<ChatUserFriend>();
        lqw.eq(ChatUserFriend::getChatUid, uid);
        List<ChatUserFriend> list = list(lqw);

        List<Long> userIds = list.stream().map(ChatUserFriend::getFriendUid).collect(Collectors.toList());
        if(userIds == null || userIds.size() == 0) {
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
}
