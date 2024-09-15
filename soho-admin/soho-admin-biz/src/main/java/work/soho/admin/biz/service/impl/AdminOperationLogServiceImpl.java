package work.soho.admin.biz.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.admin.biz.domain.AdminOperationLog;
import work.soho.admin.biz.mapper.AdminOperationLogMapper;
import work.soho.admin.biz.service.AdminOperationLogService;

@RequiredArgsConstructor
@Service
public class AdminOperationLogServiceImpl extends ServiceImpl<AdminOperationLogMapper, AdminOperationLog>
    implements AdminOperationLogService{

}