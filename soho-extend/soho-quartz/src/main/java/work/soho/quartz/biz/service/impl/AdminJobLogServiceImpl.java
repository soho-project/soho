package work.soho.quartz.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.quartz.biz.domain.AdminJobLog;
import work.soho.quartz.biz.service.AdminJobLogService;
import work.soho.quartz.biz.mapper.AdminJobLogMapper;
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




