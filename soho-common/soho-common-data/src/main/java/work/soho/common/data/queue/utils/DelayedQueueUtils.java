package work.soho.common.data.queue.utils;

import lombok.experimental.UtilityClass;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.data.queue.DelayedQueue;
import work.soho.common.data.queue.message.DefaultExecDelayedMessage;
import work.soho.common.data.queue.message.EventDelayedMessage;
import work.soho.common.data.queue.message.ExecDelayedMessage;

@UtilityClass
public class DelayedQueueUtils {
    /**
     * 添加事件延时队列
     *
     * @param message
     * @param delayMilliseconds
     */
    public void addEventDelayedMessage(Object message, long delayMilliseconds) {
        EventDelayedMessage eventDelayedMessage = new EventDelayedMessage(message, delayMilliseconds);
        SpringContextHolder.getBean(DelayedQueue.class).add(eventDelayedMessage);
    }

    /**
     * 添加延时消息
     *
     * @param message
     */
    public void addExecDelayedMessage(ExecDelayedMessage message) {
        SpringContextHolder.getBean(DelayedQueue.class).add(message);
    }

    /**
     * 添加Runnable接口延时消息队列
     *
     * @param message
     * @param delayMilliseconds
     */
    public void addExecDelayedMessage(Runnable message, long delayMilliseconds) {
        DefaultExecDelayedMessage execDelayedMessage = new DefaultExecDelayedMessage(message, delayMilliseconds);
        SpringContextHolder.getBean(DelayedQueue.class).add(execDelayedMessage);
    }
}
