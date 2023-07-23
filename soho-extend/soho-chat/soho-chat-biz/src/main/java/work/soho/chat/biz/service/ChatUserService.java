package work.soho.chat.biz.service;

import work.soho.chat.biz.domain.ChatUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface ChatUserService extends IService<ChatUser> {
    Map<String, String> getTokenInfoByUserId(Long userId);
}
