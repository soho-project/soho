package work.soho.chat.biz.service;

import work.soho.chat.biz.domain.ChatUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface ChatUserService extends IService<ChatUser> {
    Map<String, String> getTokenInfoByUserId(Long userId);

    /**
     * 根据原始信息获取用户鉴权
     *
     * @param originId
     * @param originType
     * @return
     */
    Map<String, String> getTokenInfo(String originId, String originType);

    ChatUser getByUsername(String username);
}
