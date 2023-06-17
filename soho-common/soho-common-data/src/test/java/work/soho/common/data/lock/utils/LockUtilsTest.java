package work.soho.common.data.lock.utils;

import cn.hutool.core.lang.Assert;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.common.TestCommonApplication;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@TestPropertySource(locations="classpath:/application.yml")
@SpringBootTest(classes = TestCommonApplication.class)
@Log4j2
class LockUtilsTest {

    @Test
    void getLockClient() throws ExecutionException, InterruptedException {
        //start time
        long start = System.currentTimeMillis();
        long testCount = 1000000l;
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        LockJoinTask lockJoinTask = new LockJoinTask(0l, testCount);
        ForkJoinTask<Long> rootTask = forkJoinPool.submit(lockJoinTask);
        long resultCount = rootTask.get();
        assertEquals(resultCount, testCount);
        log.info("总计加锁： {}", resultCount);

        long end = System.currentTimeMillis();
        log.info("use time: {} ms", (end -start));
    }

    public static class LockJoinTask extends RecursiveTask<Long> {
        private final Long min;
        private final Long max;

        public LockJoinTask(Long min, Long max) {
            this.min = min;
            this.max = max;
        }

        @Override
        protected Long compute() {
            if((max - min) <= 10000) {
                //直接执行不进行任务拆分
                long count = 0;
                RLock lock = LockUtils.getLockClient().getLock("test");
                for (long i = min; i < max; i++) {
                    lock.lock();
                    try {
                        //nothing todo
//                        System.out.println("hello");
                    } finally {
                        lock.unlock();
                    }
                    count++;
                }
                return count;
            }

            //任务拆分
            long middle = (max + min) >>> 1;
            LockJoinTask leftTask = new LockJoinTask(min, middle);
            leftTask.fork();
            LockJoinTask rightTask = new LockJoinTask(middle, max);
            rightTask.fork();
            return leftTask.join() + rightTask.join();
        }
    }
}
