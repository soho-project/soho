package work.soho.common.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

@Service("testjob")
public class Hello implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("Hello");
    }

    public void test(Integer id) {
        System.out.println("test by fang: Long:"+id);
    }
}
