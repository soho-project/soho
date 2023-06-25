package work.soho.admin.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.admin.domain.AdminUserLoginLog;
import work.soho.admin.mapper.AdminUserLoginLogMapper;
import work.soho.admin.service.AdminUserLoginLogService;

@RequiredArgsConstructor
@Service
public class AdminUserLoginLogServiceImpl extends ServiceImpl<AdminUserLoginLogMapper, AdminUserLoginLog>
    implements AdminUserLoginLogService{

}