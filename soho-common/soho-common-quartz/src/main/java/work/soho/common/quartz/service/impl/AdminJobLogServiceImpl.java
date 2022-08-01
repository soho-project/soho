package work.soho.common.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.common.quartz.domain.AdminJobLog;
import work.soho.common.quartz.service.AdminJobLogService;
import work.soho.common.quartz.mapper.AdminJobLogMapper;
import org.springframework.stereotype.Service;

/**
* @author i
* @description 针对表【admin_job_log(计划任务执行日志)】的数据库操作Service实现
* @createDate 2022-07-26 03:42:42
*/
@Service
public class AdminJobLogServiceImpl extends ServiceImpl<AdminJobLogMapper, AdminJobLog>
    implements AdminJobLogService{

}




