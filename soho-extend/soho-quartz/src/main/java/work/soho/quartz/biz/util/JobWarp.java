package work.soho.quartz.biz.util;

import lombok.Data;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.quartz.biz.domain.AdminJob;
import work.soho.quartz.biz.domain.AdminJobLog;
import work.soho.quartz.biz.enums.AdminJobLogEnums;
import work.soho.quartz.biz.service.AdminJobLogService;

@Data
public class JobWarp implements Job {
    private Job job;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        AdminJob val = (AdminJob) context.getMergedJobDataMap().get(JobUtil.DEFAULT_JOB_PARAMS_NAME);
        AdminJobLog log = new AdminJobLog();
        log.setJobId(val.getId());
        log.setStartTime(java.time.LocalDateTime.now());
        log.setStatus(AdminJobLogEnums.Status.TERMINATION_OF_EXECUTION.getId());
        try {
            this.log(log);
            InvokeUtil.invoke(val.getCmd());
            log.setStatus(AdminJobLogEnums.Status.EXECUTION_COMPLETED.getId());
            log.setEndTime(java.time.LocalDateTime.now());
            this.log(log);
        } catch (Exception e) {
            log.setStatus(AdminJobLogEnums.Status.TERMINATION_OF_EXECUTION.getId());
            log.setResult(e.getMessage());
            log.setEndTime(java.time.LocalDateTime.now());
            this.log(log);
            e.printStackTrace();
            //ignore
        }
    }

    /**
     * 写入运行日志
     *
     * @param adminJobLog
     */
    private void log(AdminJobLog adminJobLog) {
        AdminJobLogService adminJobLogService = SpringContextHolder.getBean(AdminJobLogService.class);
        adminJobLogService.saveOrUpdate(adminJobLog);
    }
}
