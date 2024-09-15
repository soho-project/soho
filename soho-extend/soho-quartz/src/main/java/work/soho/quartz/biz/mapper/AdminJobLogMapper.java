package work.soho.quartz.biz.mapper;

import org.apache.ibatis.annotations.Mapper;
import work.soho.quartz.biz.domain.AdminJobLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author i
* @description 针对表【admin_job_log(计划任务执行日志)】的数据库操作Mapper
* @createDate 2022-07-26 03:42:42
* @Entity work.soho.common.quartz.domain.AdminJobLog
*/
@Mapper
public interface AdminJobLogMapper extends BaseMapper<AdminJobLog> {

}




