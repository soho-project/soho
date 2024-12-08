package work.soho.common.data.queue.utils;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.common.TestCommonApplication;

import java.util.concurrent.atomic.AtomicInteger;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@TestPropertySource(locations="classpath:/application.yml")
@SpringBootTest(classes = TestCommonApplication.class)
@Log4j2
class DelayedQueueUtilsTest {

    private int count = 0;

    private AtomicInteger okCount = new AtomicInteger();

    /**
     * 测试删除延迟队列
     *
     * @throws InterruptedException
     */
    @Test
    void testExecDelayedMessage() throws InterruptedException {
        long startTs = System.currentTimeMillis();
        log.info("开始执行, ts: {}", startTs);
        for (long i = 0; i < 100; i++) {
            count++;
            log.info("入队： {}", count);
            DelayedQueueUtils.addExecDelayedMessage(new TestRun(i), 1000, i, null);
        }
        for (long i = 0; i < 100; i++) {
            if(i % 2 == 0) {
                DelayedQueueUtils.removeById(i);
            }
        }
        long endTs = System.currentTimeMillis();
        System.out.println("耗时： " + (endTs - startTs));
        Thread.sleep(15000);
    }

    @Test
    void addExecDelayedMessage() throws InterruptedException {
        long startTs = System.currentTimeMillis();
        log.info("开始执行, ts: {}", startTs);
        for (int i = 0; i < 100; i++) {
            count++;
            log.info("入队： {}", count);
            DelayedQueueUtils.addEventDelayedMessage(new TestRun(i), 100);
        }
        long endTs = System.currentTimeMillis();
        System.out.println("耗时： " + (endTs - startTs));
        Thread.sleep(15000);
    }

    /**
     * 测试延时，计算qps
     *
     * @throws InterruptedException
     */
    @Test
    void addExecDelayedMessage2() throws InterruptedException {
        int total = 100000;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < total; i++) {
            DelayedQueueUtils.addExecDelayedMessage(new Runnable() {
                @Override
                public void run() {
                    int currentCount = okCount.incrementAndGet();
                    System.out.println("okCount: " + currentCount);
                    if (currentCount == total) {
                        log.info("耗时： {}", (System.currentTimeMillis() - startTime));
                        log.info("QPS {}", (total / ((System.currentTimeMillis() - startTime))) * 1000);
                    }
                }
            }, 1);
        }
        Thread.sleep(9000);
    }

    static class TestRun implements Runnable {
        private final long msg;

        TestRun(long msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            System.out.println("执行消息 " + msg);
            log.info("消费执行消息： {}", msg);
        }
    }
}


