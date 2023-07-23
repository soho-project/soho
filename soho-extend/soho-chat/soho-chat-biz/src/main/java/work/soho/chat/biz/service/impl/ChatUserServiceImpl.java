package work.soho.chat.biz.service.impl;

import cn.hutool.core.lang.Assert;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.admin.common.security.service.SohoTokenService;
import work.soho.admin.common.security.userdetails.SohoUserDetails;
import work.soho.chat.api.Constants;
import work.soho.chat.biz.domain.ChatUser;
import work.soho.chat.biz.mapper.ChatUserMapper;
import work.soho.chat.biz.service.ChatUserService;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class ChatUserServiceImpl extends ServiceImpl<ChatUserMapper, ChatUser>
    implements ChatUserService{

    private final SohoTokenService sohoTokenService;

    /**
     * 创建指定用户的令牌
     *
     * @param userId
     * @return
     */
    @Override
    public Map<String, String> getTokenInfoByUserId(Long userId) {
        ChatUser chatUser = getById(userId);
        Assert.notNull(chatUser, "用户不存在");
        SohoUserDetails sohoUserDetails = new SohoUserDetails();
        sohoUserDetails.setId(userId);
        sohoUserDetails.setUsername(chatUser.getUsername());
        sohoUserDetails.setAuthorities(AuthorityUtils.createAuthorityList(Constants.ROLE_NAME));
        return sohoTokenService.createTokenInfo(sohoUserDetails);
    }
}
