package work.soho.user.biz.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.service.SohoUserDetailsService;
import work.soho.common.security.service.impl.TokenServiceImpl;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.api.dto.ThridOauthDto;
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

    @Override
    public Map<String, String> loginWithThridOauth(ThridOauthDto thridOauthDto) {
        // 查询用户是否已经存在
        LambdaQueryWrapper<UserOauth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserOauth::getOpenId, thridOauthDto.getOpenId());
        queryWrapper.eq(UserOauth::getType, thridOauthDto.getPlatformId());
        UserOauth userOauth = getOne(queryWrapper, false);
        UserInfo userInfo = null;
        if(userOauth == null) {
            // 根据用户手机号合并用户
            if(thridOauthDto.getPhone()!=null) {
                userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getPhone, thridOauthDto.getPhone()));
            }

            if(userInfo == null) {
                // 创建用户
                userInfo = new UserInfo();
                userInfo.setCode(IDGeneratorUtils.snowflake().toString());
                // 各个平台的用户名可能一样， 所以用户名添加个前缀
                userInfo.setUsername("o"+thridOauthDto.getPlatformId() + "_" + thridOauthDto.getUsername());
                userInfo.setNickname(thridOauthDto.getNickname());
                userInfo.setNickname("三方"+System.currentTimeMillis());
                userInfo.setAvatar(StringUtils.isBlank(thridOauthDto.getAvatar()) ? userSysConfig.getDefaultAvatar() : thridOauthDto.getAvatar());
                userInfo.setStatus(UserInfoEnums.Status.NORMAL.getId());
                userInfo.setSex(thridOauthDto.getGender());
                userInfo.setPhone(thridOauthDto.getPhone());
                userInfoMapper.insert(userInfo);
            }

            Assert.notNull(userInfo, "创建用户失败");

            userOauth = new UserOauth();
            userOauth.setOpenId(thridOauthDto.getOpenId());
            userOauth.setUnionId(thridOauthDto.getUnionId());
            userOauth.setType(thridOauthDto.getPlatformId());
            userOauth.setUid(userInfo.getId());
            save(userOauth);
        } else {
            userInfo = userInfoMapper.selectById(userOauth.getUid());
        }

        Assert.notNull(userInfo, "用户不存在，登录失败");
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