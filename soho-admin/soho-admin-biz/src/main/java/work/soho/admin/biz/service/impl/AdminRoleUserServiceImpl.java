package work.soho.admin.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.admin.biz.domain.AdminRoleUser;
import work.soho.admin.biz.service.AdminRoleUserService;
import work.soho.admin.biz.mapper.AdminRoleUserMapper;
import org.springframework.stereotype.Service;

/**
* @author i
* @description 针对表【admin_role_user】的数据库操作Service实现
* @createDate 2022-03-31 00:19:02
*/
@Service
@RequiredArgsConstructor
public class AdminRoleUserServiceImpl extends ServiceImpl<AdminRoleUserMapper, AdminRoleUser>
        implements AdminRoleUserService {

}
