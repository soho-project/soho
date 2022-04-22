package work.soho.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.admin.domain.AdminUserLoginLog;
import work.soho.admin.service.AdminUserLoginLogService;
import work.soho.admin.mapper.AdminUserLoginLogMapper;
import org.springframework.stereotype.Service;

/**
* @author i
* @description 针对表【admin_user_login_log(用户登录日志)】的数据库操作Service实现
* @createDate 2022-04-22 23:15:51
*/
@Service
public class AdminUserLoginLogServiceImpl extends ServiceImpl<AdminUserLoginLogMapper, AdminUserLoginLog>
    implements AdminUserLoginLogService{

}




