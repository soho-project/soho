package work.soho.admin.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.admin.domain.AdminOperationLog;
import work.soho.admin.mapper.AdminOperationLogMapper;
import work.soho.admin.service.AdminOperationLogService;

@RequiredArgsConstructor
@Service
public class AdminOperationLogServiceImpl extends ServiceImpl<AdminOperationLogMapper, AdminOperationLog>
    implements AdminOperationLogService{

}