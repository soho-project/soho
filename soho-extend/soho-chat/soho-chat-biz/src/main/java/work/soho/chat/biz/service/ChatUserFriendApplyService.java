package work.soho.chat.biz.service;

import work.soho.chat.biz.domain.ChatUserFriendApply;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ChatUserFriendApplyService extends IService<ChatUserFriendApply> {
    Boolean createApply(ChatUserFriendApply chatUserFriendApply);
}
