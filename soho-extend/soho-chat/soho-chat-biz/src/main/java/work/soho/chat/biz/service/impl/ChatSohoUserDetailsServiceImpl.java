package work.soho.chat.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import work.soho.admin.common.security.service.SohoUserDetailsService;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.api.Constants;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.service.ChatUserService;

/**
 * security impl
 *
 * 获取聊天用户信息
 */
@RequiredArgsConstructor
public class ChatSohoUserDetailsServiceImpl implements SohoUserDetailsService {
    /**
     * 用户服务
     */
    private final ChatUserService chatUserService;

    /**
     * 获取指定用户名用户信息
     *
     * @param username
     * @return
     */
    @Override
    public SohoUserDetails loadUserByUsername(String username) {
        SohoUserDetails sohoUserDetails = new SohoUserDetails();
        ChatUser chatUser = chatUserService.getByUsername(username);
        sohoUserDetails.setUsername(chatUser.getUsername());
        sohoUserDetails.setId(chatUser.getId());
        sohoUserDetails.setAuthorities(AuthorityUtils.createAuthorityList(getUserRoleName()));
        return sohoUserDetails;
    }

    @Override
    public String getUserRoleName() {
        return Constants.ROLE_NAME;
    }
}
