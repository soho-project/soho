package work.soho.common.data.queue.message;

/**
 * 事件消息
 */
public class EventDelayedMessage extends DelayedMessage{
    public EventDelayedMessage(Object message, long delayTime) {
        super(message, delayTime);
    }
}
