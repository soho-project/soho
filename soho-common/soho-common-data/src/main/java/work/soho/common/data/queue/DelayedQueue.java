package work.soho.common.data.queue;

import org.springframework.stereotype.Component;
import work.soho.common.data.queue.message.DelayedMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

@Component
public class DelayedQueue {
    private final BlockingQueue<DelayedMessage> queue = new DelayQueue<>();

    /**
     * 添加延时队列任务到队列
     *
     * @param message
     */
    public void add(DelayedMessage message) {
        queue.offer(message);
    }

    /**
     * 获取一个延时队列
     *
     * @return
     * @throws InterruptedException
     */
    public DelayedMessage take() throws InterruptedException {
        return (DelayedMessage) queue.take().getMessage();
    }
}
