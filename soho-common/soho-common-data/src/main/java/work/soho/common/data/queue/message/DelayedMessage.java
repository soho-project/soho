package work.soho.common.data.queue.message;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedMessage<T> implements Delayed {
    private final long delayTime;
    private final T message;

    DelayedMessage(T message, long delayTime) {
        this.message = message;
        this.delayTime = System.currentTimeMillis() + delayTime;
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
