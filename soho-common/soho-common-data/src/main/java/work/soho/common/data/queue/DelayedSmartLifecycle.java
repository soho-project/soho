package work.soho.common.data.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import work.soho.common.data.queue.message.DelayedMessage;
import work.soho.common.data.queue.store.StoreInterface;

import java.io.IOException;

/**
 * TODO 监管延时队列生命周期； 防止停止程序的时候还有没有消费的消息队列
 * 消息队列在停止的时候持久化，在下次停止的时候重新进入队列
 */
@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "soho.delayed-queue.store.enabled", havingValue = "true")
public class DelayedSmartLifecycle implements SmartLifecycle {
    private boolean isRuning = false;

    private final DelayedQueue delayedQueue;

    private final StoreInterface storeInterface;

    @Override
    public void start() {
        do {
            try {
                DelayedMessage message = storeInterface.pop();
                if(message == null) {
                    break;
                }
                delayedQueue.getQueue().put(message);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while(true);
        isRuning = true;
    }

    @Override
    public void stop() {
        Object[] list = delayedQueue.getQueue().toArray();
        for (Object o : list) {
            try {
                storeInterface.push((DelayedMessage) o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isRuning = false;
    }

    @Override
    public boolean isRunning() {
        return isRuning;
    }
}
