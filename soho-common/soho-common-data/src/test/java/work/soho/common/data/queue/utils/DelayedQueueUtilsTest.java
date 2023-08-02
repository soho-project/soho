package work.soho.common.data.queue.utils;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.common.TestCommonApplication;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@TestPropertySource(locations="classpath:/application.yml")
@SpringBootTest(classes = TestCommonApplication.class)
@Log4j2
class DelayedQueueUtilsTest {

    @Test
    void addExecDelayedMessage() throws InterruptedException {
        System.out.println("test by fang");
        DelayedQueueUtils.addEventDelayedMessage(new TestRun(), 3000);
        Thread.sleep(5000);
    }

    @Test
    void addExecDelayedMessage2() throws InterruptedException {
        System.out.println("test by fang");
        DelayedQueueUtils.addExecDelayedMessage(new Runnable() {
            @Override
            public void run() {
                System.out.println("runable 接口任务。。。。。。");
            }
        }, 3000);
        Thread.sleep(5000);
    }
}

class TestRun implements Runnable {

    @Override
    public void run() {
        System.out.println(".......................... 延时队列测试");
    }
}
