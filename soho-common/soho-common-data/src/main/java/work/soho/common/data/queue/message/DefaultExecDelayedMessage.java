package work.soho.common.data.queue.message;

/**
 * 默认可执行延时任务消息
 */
public class DefaultExecDelayedMessage extends ExecDelayedMessage{
    public DefaultExecDelayedMessage(Runnable message, long delayTime) {
        this(message, delayTime, null, null);
    }

    public DefaultExecDelayedMessage(Runnable message,long delayTime, Long id, String groupName) {
        super(message,delayTime,id, groupName);
    }

    @Override
    public void run() {
        Runnable runnable = (Runnable) getMessage();
        runnable.run();
    }
}
