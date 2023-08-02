package work.soho.common.data.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.data.queue.message.DelayedMessage;
import work.soho.common.data.queue.message.EventDelayedMessage;
import work.soho.common.data.queue.message.ExecDelayedMessage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class DelayedQueueProduct implements ApplicationRunner {
    /**
     * 队列
     */
    private final DelayedQueue delayedQueue;

    /**
     * 动态线程池
     */
    ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        while(true) {
            DelayedMessage message = delayedQueue.take();
            //进行业务处理
            if(message instanceof EventDelayedMessage) {
                executor.submit(()->{
                    SpringContextHolder.getApplicationContext().publishEvent(message);
                });
            } else if (message instanceof ExecDelayedMessage) {
                executor.submit((Runnable) message);
            }
        }
    }
}
