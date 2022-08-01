package work.soho.common.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import work.soho.common.quartz.util.InvokeUtil;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@SpringBootApplication
public class SohoCommonQuartzApplication {

    public static void main(String[] args) {
        SpringApplication.run(SohoCommonQuartzApplication.class, args);

        //InvokeUtil.invoke("work.soho.common.quartz.Hello::test(2000)");
        return;

//        try {
//            // Grab the Scheduler instance from the Factory
//            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//
//            JobDetail job = newJob(Hello.class)
//                    .withIdentity("job1", "group1")
//                    .build();
//
//            // Trigger the job to run now, and then repeat every 40 seconds
//
//            Trigger trigger = TriggerBuilder.newTrigger()
//                    .withIdentity("job1dd", "group12")
//                    .startNow()
//                    .withSchedule(simpleSchedule()
//                            .withIntervalInSeconds(5)
//                            .repeatForever())
//                    .build();
//
//
//
//
//            // and start it off
//            scheduler.start();
//            // Tell quartz to schedule the job using our trigger
//            scheduler.scheduleJob(job, trigger);
////            scheduler.shutdown();
//
//        } catch (SchedulerException se) {
//            se.printStackTrace();
//        }
    }

}
