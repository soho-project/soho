package work.soho.common.data.queue.store;

import work.soho.common.data.queue.message.DelayedMessage;

import java.io.IOException;

/**
 * 持久化消息存储
 */
public interface StoreInterface {
    /**
     * 压入消息到存储
     * @param delayedMessage
     */
    void push(DelayedMessage delayedMessage) throws IOException;

    /**
     * 弹出消息
     * @param delayedMessage
     */
    DelayedMessage pop() throws IOException;
}
