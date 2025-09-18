package work.soho.common.ratelimiter;

/**
 * 固定窗口算法; 单线程版本
 */
public class FixedWindowRateLimiter implements RateLimiterInterface {
    // 时间窗口大小，单位毫秒
    private final long windowSizeInMillis;
    // 时间窗口内允许的最大请求数
    private final int maxRequestCount;
    // 当前窗口内的请求计数
    private int count = 0;
    // 当前窗口的开始时间戳
    private long windowStart;

    public FixedWindowRateLimiter(int maxRequestCount, long windowSizeInMillis) {
        this.maxRequestCount = maxRequestCount;
        this.windowSizeInMillis = windowSizeInMillis;
        this.windowStart = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();

        // 检查是否进入下一个时间窗口
        if (currentTime - windowStart >= windowSizeInMillis) {
            // 重置窗口和计数器
            windowStart = currentTime;
            count = 0;
        }

        // 检查是否超过限制
        if (count < maxRequestCount) {
            count++;
            return true;
        }
        return false;
    }

    @Override
    public boolean tryAcquire() {
        return allowRequest();
    }
}