package work.soho.common.data.queue.message;

/**
 * 可执行消息队列
 */
public abstract class ExecDelayedMessage extends DelayedMessage implements Runnable {
    ExecDelayedMessage(Object message, long delayTime) {
        super(message, delayTime);
    }
}
