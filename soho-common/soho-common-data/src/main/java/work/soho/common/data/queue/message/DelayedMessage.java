package work.soho.common.data.queue.message;

import lombok.Getter;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedMessage<T> implements Delayed {
    public static final String DEFAULT_GROUP_NAME = "default";
    private long delayTime;
    private T message;
    @Getter
    private Long id = null;
    @Getter
    private String groupName = DEFAULT_GROUP_NAME;

    public DelayedMessage(T message, long delayTime) {
        this(message, delayTime, null, null);
    }

    public DelayedMessage(T message, long delayTime, Long id, String groupName) {
        this.message = message;
        this.delayTime = System.currentTimeMillis() + delayTime;
        this.id = id;
        if(groupName != null) {
            this.groupName = groupName;
        }
    }

    @Override
    public boolean equals(Object o) {
        if(id == null) {
            return false;
        }

        if(o instanceof Long) {
            return id.equals(o);
        }

        if(!(o instanceof DelayedMessage<?>) || groupName == null) {
            return false;
        }

        return id.equals(((DelayedMessage<?>) o).getId()) && groupName.equals(((DelayedMessage<?>) o).getGroupName());
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long remainingDelay = delayTime - System.currentTimeMillis();
        return unit.convert(remainingDelay, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        return Long.compare(this.delayTime, ((DelayedMessage<?>) other).delayTime);
    }

    public T getMessage() {
        return message;
    }
}
