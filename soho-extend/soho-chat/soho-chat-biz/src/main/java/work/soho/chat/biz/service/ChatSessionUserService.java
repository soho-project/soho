package work.soho.chat.biz.service;

import work.soho.chat.biz.domain.ChatSessionUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ChatSessionUserService extends IService<ChatSessionUser> {
    List<ChatSessionUser> getSessionUserList(Long sessionId);
}
