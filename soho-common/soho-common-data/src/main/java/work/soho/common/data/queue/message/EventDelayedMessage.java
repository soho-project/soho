package work.soho.common.data.queue.message;

/**
 * 事件消息
 */
public class EventDelayedMessage extends DelayedMessage{
    public EventDelayedMessage(Object message, long delayTime) {
        this(message, delayTime, null, null);
    }

    public EventDelayedMessage(Object message,long delayTime,  Long id, String groupName) {
        super(message,delayTime,id, groupName);
    }
}
