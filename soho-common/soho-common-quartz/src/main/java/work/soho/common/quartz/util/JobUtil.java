package work.soho.common.quartz.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.quartz.*;
import work.soho.common.quartz.domain.AdminJob;
import static org.quartz.JobBuilder.newJob;

@UtilityClass
public class JobUtil {
    /**
     * 默认计划任务组
     */
    public static final String DEFAULT_GROUP_NAME = "default";

    public static final String DEFAULT_JOB_PARAMS_NAME = "PARAMS_NAME";

    /**
     * 创建计划任务
     *
     * @param scheduler
     * @param adminJob
     * @throws SchedulerException
     */
    @SneakyThrows
    public void buildJob(Scheduler scheduler, AdminJob adminJob) {
        JobKey jobKey = getJobKey(adminJob);
        if(scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }

        //生成job
        JobDetail job = newJob(JobWarp.class)
                .withIdentity(jobKey)
                .build();
        job.getJobDataMap().put(DEFAULT_JOB_PARAMS_NAME, adminJob);
        //创建触发器
        TriggerKey triggerKey = new TriggerKey(adminJob.getId().toString(), DEFAULT_GROUP_NAME);
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(adminJob.getCron());
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                .withSchedule(cronScheduleBuilder).build();
        scheduler.scheduleJob(job, trigger);
        //检查任务状态
        if(adminJob.getStatus() == 0) {
            scheduler.pauseJob(jobKey);
        }
    }

    /**
     * 删除job
     *
     * @param scheduler
     * @param adminJob
     * @throws SchedulerException
     */
    @SneakyThrows
    public void removeJob(Scheduler scheduler, AdminJob adminJob) {
        JobKey jobKey = getJobKey(adminJob);
        if(scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
    }

    /**
     * 立即执行计划任务
     *
     * @param scheduler
     * @param adminJob
     * @throws SchedulerException
     */
    @SneakyThrows
    public void runJob(Scheduler scheduler, AdminJob adminJob) {
        JobKey jobKey = getJobKey(adminJob);
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(DEFAULT_JOB_PARAMS_NAME, adminJob);
        scheduler.triggerJob(jobKey, dataMap);
    }

    public JobKey getJobKey(AdminJob adminJob) {
        return new JobKey(adminJob.getId().toString());
    }
}
