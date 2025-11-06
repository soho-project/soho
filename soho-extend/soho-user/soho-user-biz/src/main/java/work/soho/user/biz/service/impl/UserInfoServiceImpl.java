package work.soho.user.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.security.service.SohoUserDetailsService;
import work.soho.common.security.userdetails.SohoUserDetails;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.user.api.service.UserApiService;
import work.soho.user.biz.domain.UserInfo;
import work.soho.user.biz.mapper.UserInfoMapper;
import work.soho.user.biz.service.UserInfoService;

/**
* @author fang
* @description 针对表【user_info(用户信息;;option:id~username)】的数据库操作Service实现
* @createDate 2022-11-28 10:08:51
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService, UserApiService, SohoUserDetailsService {

    @Override
    public UserInfoDto getUserById(Long id) {
        return BeanUtils.copy(getById(id), UserInfoDto.class);
    }

    @Override
    public UserInfoDto getUserInfoByPhone(String phone) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getPhone, phone);
        UserInfo userInfo = getOne(queryWrapper);
        if (userInfo == null) {
            return null;
        }
        return BeanUtils.copy(userInfo, UserInfoDto.class);
    }

    @Override
    public Boolean verificationUserInfoPayPassword(Long userId, String payPassword) {
        // TODO 实现支付密码验证
        return true;
    }

    @Override
    public UserInfoDto updateUserInfoDto(UserInfoDto userInfoDto) {
        if (userInfoDto != null) {
            UserInfo userInfo = BeanUtils.copy(userInfoDto, UserInfo.class);
            updateById(userInfo);
            return BeanUtils.copy(userInfo, UserInfoDto.class);
        }
        return null;
    }

    @Override
    public SohoUserDetails loadUserByUsername(String username) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUsername, username);
        UserInfo userInfo = getOne(queryWrapper);
        if (userInfo != null) {
            SohoUserDetails userDetails = new SohoUserDetails();
            userDetails.setUsername(String.valueOf(userInfo.getPhone()));
            userDetails.setPassword(userInfo.getPassword());
            userDetails.setId(userInfo.getId());
            userDetails.setAuthorities(AuthorityUtils.createAuthorityList(getUserRoleName()));
            return userDetails;
        }
        return null;
    }

    @Override
    public String getUserRoleName() {
        return "user";
    }
}




