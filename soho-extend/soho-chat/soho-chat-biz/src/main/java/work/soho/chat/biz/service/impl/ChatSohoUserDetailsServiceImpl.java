package work.soho.chat.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import work.soho.common.security.service.SohoUserDetailsService;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.chat.api.Constants;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.mapper.ChatUserMapper;

/**
 * security impl
 *
 * 获取聊天用户信息
 */
@Service
@RequiredArgsConstructor
@Lazy
public class ChatSohoUserDetailsServiceImpl implements SohoUserDetailsService {
    /**
     * 用户服务
     */
    private final ChatUserMapper chatUserMapper;

    /**
     * 获取指定用户名用户信息
     *
     * @param username
     * @return
     */
    @Override
    public SohoUserDetails loadUserByUsername(String username) {
        SohoUserDetails sohoUserDetails = new SohoUserDetails();
        LambdaQueryWrapper<ChatUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatUser::getUsername, username);
        ChatUser chatUser = chatUserMapper.selectOne(lambdaQueryWrapper);
        sohoUserDetails.setUsername(chatUser.getUsername());
        sohoUserDetails.setPassword(chatUser.getPassword());
        sohoUserDetails.setId(chatUser.getId());
        sohoUserDetails.setAuthorities(AuthorityUtils.createAuthorityList(getUserRoleName()));
        return sohoUserDetails;
    }

    @Override
    public String getUserRoleName() {
        return Constants.ROLE_NAME;
    }
}
