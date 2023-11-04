package work.soho.chat.biz.service;

import work.soho.chat.biz.domain.ChatGroupUser;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ChatGroupUserService extends IService<ChatGroupUser> {
    ChatGroupUser getByUid(Long id,Long uid);

    void restoreUserBanned(ChatGroupUser chatGroupUser);

    void restoreBanned();
}
