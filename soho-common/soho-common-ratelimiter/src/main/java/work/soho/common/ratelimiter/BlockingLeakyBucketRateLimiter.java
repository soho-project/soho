package work.soho.common.ratelimiter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 阻塞式漏桶限流算法
 */
public class BlockingLeakyBucketRateLimiter implements RateLimiterInterface {
    private final long capacity;
    private final long leaksPerSecond;
    private long water;
    private long lastLeakTime;
    private final ReentrantLock lock;
    private final Condition condition;

    public BlockingLeakyBucketRateLimiter(long capacity, long leaksPerSecond) {
        this.capacity = capacity;
        this.leaksPerSecond = leaksPerSecond;
        this.water = 0;
        this.lastLeakTime = System.currentTimeMillis();
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
    }

    public boolean tryAcquire() throws InterruptedException {
        return tryAcquire(1, 0, TimeUnit.MILLISECONDS);
    }

    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        return tryAcquire(1, timeout, unit);
    }

    public boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException {
        long waitTime = unit.toMillis(timeout);
        long deadline = System.currentTimeMillis() + waitTime;

        lock.lock();
        try {
            while (true) {
                leak();

                if (water + permits <= capacity) {
                    water += permits;
                    return true;
                }

                if (waitTime <= 0) {
                    return false;
                }

                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) {
                    return false;
                }

                condition.await(remaining, TimeUnit.MILLISECONDS);
            }
        } finally {
            lock.unlock();
        }
    }

    private void leak() {
        long now = System.currentTimeMillis();
        long elapsedTime = now - lastLeakTime;

        long leaks = elapsedTime * leaksPerSecond / 1000;
        if (leaks > 0) {
            water = Math.max(0, water - leaks);
            lastLeakTime = now;
        }
    }
}