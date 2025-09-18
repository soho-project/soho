package work.soho.common.ratelimiter;

/**
 * 基于同步的固定窗口算法实现
 */
public class SynchronizedFixedWindowRateLimiter implements RateLimiterInterface {
    private final long windowSizeInMillis;
    private final int maxRequestCount;
    private int count = 0;
    private long windowStart;

    public SynchronizedFixedWindowRateLimiter(int maxRequestCount, long windowSizeInMillis) {
        this.maxRequestCount = maxRequestCount;
        this.windowSizeInMillis = windowSizeInMillis;
        this.windowStart = System.currentTimeMillis();
    }

    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - windowStart > windowSizeInMillis) {
            windowStart = currentTime;
            count = 0;
        }

        if (count < maxRequestCount) {
            count++;
            return true;
        }
        return false;
    }
}