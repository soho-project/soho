package work.soho.chat.biz.service;

import work.soho.chat.biz.domain.ChatUserFriend;
import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.chat.biz.vo.UserFriendVO;

import java.util.List;

public interface ChatUserFriendService extends IService<ChatUserFriend> {
    List<UserFriendVO> getListByUid(Long uid);

    Boolean applyFriend(Long uid, Long friendId);

    /**
     * 创建一个好友信息
     *
     * 检查是否存在，如果存在则进行更新
     *
     * @param chatUserFriend
     */
    void createFriend(ChatUserFriend chatUserFriend);
}
