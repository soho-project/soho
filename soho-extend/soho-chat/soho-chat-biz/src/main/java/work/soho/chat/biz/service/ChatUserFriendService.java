package work.soho.chat.biz.service;

import work.soho.chat.biz.domain.ChatUserFriend;
import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.chat.biz.vo.UserFriendVO;

import java.util.List;

public interface ChatUserFriendService extends IService<ChatUserFriend> {
    List<UserFriendVO> getListByUid(Long uid);
}
