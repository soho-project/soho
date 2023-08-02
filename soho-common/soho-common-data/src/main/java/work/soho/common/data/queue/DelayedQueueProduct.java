package work.soho.common.data.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.data.queue.message.DelayedMessage;
import work.soho.common.data.queue.message.EventDelayedMessage;
import work.soho.common.data.queue.message.ExecDelayedMessage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
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
        (new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        DelayedMessage message = delayedQueue.take();
                        log.info("当前消费延时消息: {}", message);
                        //进行业务处理
                        if(message instanceof EventDelayedMessage) {
                            DelayedMessage finalMessage = message;
                            executor.submit(()->{
                                SpringContextHolder.getApplicationContext().publishEvent(finalMessage.getMessage());
                            });
                        } else if (message instanceof ExecDelayedMessage) {
                            executor.submit((Runnable) message);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        })).start();
    }
}
