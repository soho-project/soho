package work.soho.common.data.queue.message;

/**
 * 默认可执行延时任务消息
 */
public class DefaultExecDelayedMessage extends ExecDelayedMessage{
    public DefaultExecDelayedMessage(Runnable message, long delayTime) {
        super(message, delayTime);
    }

    @Override
    public void run() {
        Runnable runnable = (Runnable) getMessage();
        runnable.run();
    }
}
