package work.soho.admin.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.admin.biz.domain.AdminUserLoginLog;
import work.soho.admin.biz.mapper.AdminUserLoginLogMapper;
import work.soho.admin.biz.service.AdminUserLoginLogService;

@RequiredArgsConstructor
@Service
public class AdminUserLoginLogServiceImpl extends ServiceImpl<AdminUserLoginLogMapper, AdminUserLoginLog>
    implements AdminUserLoginLogService{

}