package work.soho.common.data.queue.utils;

import lombok.experimental.UtilityClass;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.data.queue.DelayedQueue;
import work.soho.common.data.queue.message.DefaultExecDelayedMessage;
import work.soho.common.data.queue.message.DelayedMessage;
import work.soho.common.data.queue.message.EventDelayedMessage;

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
    public void addExecDelayedMessage(DelayedMessage<?> message) {
        SpringContextHolder.getBean(DelayedQueue.class).add(message);
    }


    /**
     * 添加Runnable接口延时消息队列
     *
     * @param message
     * @param delayMilliseconds
     */
    public void addExecDelayedMessage(Runnable message, long delayMilliseconds) {
        addExecDelayedMessage(message, delayMilliseconds, null, null);
    }

    /**
     * 添加Runnable接口延时消息队列
     *
     * @param message
     * @param delayMilliseconds
     * @param id
     */
    public void addExecDelayedMessage(Runnable message, long delayMilliseconds, Long id, String groupName) {
        DefaultExecDelayedMessage execDelayedMessage = new DefaultExecDelayedMessage(message, delayMilliseconds, id, groupName);
        SpringContextHolder.getBean(DelayedQueue.class).add(execDelayedMessage);
    }

    /**
     * 删除默认组延时队列任务
     *
     * @param id
     */
    public void removeById(Long id) {
        SpringContextHolder.getBean(DelayedQueue.class).removeById(id);
    }

    /**
     * 删除延时任务
     *
     * @param id
     * @param groupName
     */
    public void remove(Long id, String groupName) {
        SpringContextHolder.getBean(DelayedQueue.class).remove(id, groupName);
    }

    /**
     * 删除指定延时消息
     *
     * @param message
     */
    public void remove(DelayedMessage<?> message) {
        SpringContextHolder.getBean(DelayedQueue.class).remove(message);
    }
}
