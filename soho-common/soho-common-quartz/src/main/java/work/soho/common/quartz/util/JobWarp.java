package work.soho.common.quartz.util;

import lombok.Data;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import work.soho.common.quartz.domain.AdminJob;

@Data
public class JobWarp implements Job {
    private Job job;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        AdminJob val = (AdminJob) context.getMergedJobDataMap().get(JobUtil.DEFAULT_JOB_PARAMS_NAME);
        try {
            InvokeUtil.invoke(val.getCmd());
        } catch (Exception e) {
            e.printStackTrace();
            //ignore
        }
    }
}
