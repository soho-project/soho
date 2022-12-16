package work.soho.user.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    implements UserInfoService{

}




