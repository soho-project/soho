package work.soho.common.ratelimiter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * 滑动窗口限流器
 */
public class SlidingWindowRateLimiter implements RateLimiterInterface {
    // 时间窗口内允许的最大请求数
    private final int maxRequests;
    // 时间窗口大小（毫秒）
    private final long windowSizeInMillis;
    // 请求时间戳队列
    private final Queue<Long> requestTimestamps;

    /**
     * 构造函数
     * @param maxRequests 时间窗口内允许的最大请求数
     * @param windowSize 时间窗口大小
     * @param timeUnit 时间单位
     */
    public SlidingWindowRateLimiter(int maxRequests, long windowSize, TimeUnit timeUnit) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = timeUnit.toMillis(windowSize);
        this.requestTimestamps = new LinkedList<>();
    }

    /**
     * 尝试获取请求许可
     * @return true-允许请求，false-拒绝请求
     */
    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();

        // 移除过期的时间戳（超出当前时间窗口）
        while (!requestTimestamps.isEmpty() &&
                currentTime - requestTimestamps.peek() > windowSizeInMillis) {
            requestTimestamps.poll();
        }

        // 检查当前请求数是否超过限制
        if (requestTimestamps.size() < maxRequests) {
            requestTimestamps.offer(currentTime);
            return true;
        }

        return false;
    }

    /**
     * 获取当前窗口内的请求数量
     */
    public synchronized int getCurrentRequests() {
        long currentTime = System.currentTimeMillis();

        // 清理过期请求
        while (!requestTimestamps.isEmpty() &&
                currentTime - requestTimestamps.peek() > windowSizeInMillis) {
            requestTimestamps.poll();
        }

        return requestTimestamps.size();
    }
}