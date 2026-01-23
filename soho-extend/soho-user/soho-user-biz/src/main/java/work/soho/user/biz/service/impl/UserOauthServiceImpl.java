package work.soho.user.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.security.service.SohoUserDetailsService;
import work.soho.common.security.service.impl.TokenServiceImpl;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.biz.config.UserSysConfig;
import work.soho.user.biz.domain.UserInfo;
import work.soho.user.biz.domain.UserOauth;
import work.soho.user.biz.enums.UserInfoEnums;
import work.soho.user.biz.enums.UserOauthEnums;
import work.soho.user.biz.mapper.UserInfoMapper;
import work.soho.user.biz.mapper.UserOauthMapper;
import work.soho.user.biz.service.UserOauthService;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserOauthServiceImpl extends ServiceImpl<UserOauthMapper, UserOauth>
    implements UserOauthService{

    private final TokenServiceImpl tokenService;

    private final Map<String, SohoUserDetailsService> detailsServiceMap;

    private final UserInfoMapper userInfoMapper;

    private final UserSysConfig userSysConfig;

    @Override
    public Map<String, String> loginWithCode(String code) {
        // TODO 实现各个平台根据code获取用户信息
        UserOauth userOauth = new UserOauth();
        userOauth.setOpenId(IDGeneratorUtils.uuid());
        userOauth.setUnionId(IDGeneratorUtils.uuid());
        userOauth.setType(UserOauthEnums.Type.WECHAT_MINI_PROGRAM.getId());
        return login(userOauth);
    }

    @Override
    public Map<String, String> login(UserOauth userOauth) {
        UserInfo userInfo = null;
        // 检查有没有用户ID
        if(userOauth.getUid() == null) {
            userInfo = new UserInfo();
            userInfo.setCode(IDGeneratorUtils.uuid());
            userInfo.setUsername(IDGeneratorUtils.uuid());
            userInfo.setNickname("微信用户"+System.currentTimeMillis());
//            userInfo.setPassword(IDGeneratorUtils.uuid());
            userInfo.setAvatar(userSysConfig.getDefaultAvatar());
            userInfo.setStatus(UserInfoEnums.Status.NORMAL.getId());

            userInfoMapper.insert(userInfo);

            userInfo.setNickname("微信用户"+userInfo.getId());
            userInfo.setUsername("wechat_" + userInfo.getId());
            userInfoMapper.updateById(userInfo);

            userOauth.setUid(userInfo.getId());
            saveOrUpdate(userOauth);
        } else {
            userInfo = userInfoMapper.selectById(userOauth.getUid());
        }
        return commonLogin(userInfo);
    }

    private Map<String, String> commonLogin(UserInfo userInfo) {
        Authentication authentication = null;
        SohoUserDetails loginUser = null;
        try{
            for (SohoUserDetailsService service : detailsServiceMap.values()) {
                if(service.getUserRoleName().equals("user")) {
                    loginUser = service.loadUserByUsername(userInfo.getUsername());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("登录失败");
        }
        Map<String, String> token = tokenService.createTokenInfo(loginUser);
        return token;
    }
}