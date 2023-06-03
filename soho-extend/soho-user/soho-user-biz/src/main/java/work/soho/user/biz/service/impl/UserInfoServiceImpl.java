package work.soho.user.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.common.core.util.BeanUtils;
import work.soho.user.api.dto.UserInfoDto;
import work.soho.user.api.service.UserApiService;
import work.soho.user.biz.domain.UserInfo;
import work.soho.user.biz.service.UserInfoService;
import work.soho.user.biz.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author fang
* @description 针对表【user_info(用户信息;;option:id~username)】的数据库操作Service实现
* @createDate 2022-11-28 10:08:51
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService, UserApiService {

    @Override
    public UserInfoDto getUserById(Long id) {
        return BeanUtils.copy(getById(id), UserInfoDto.class);
    }
}




