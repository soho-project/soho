package work.soho.common.ratelimiter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 滑动窗口日志限流算法
 * @author soho
 * @date 2023/9/5 14:05
 */
public class SlidingWindowLogRateLimiter implements RateLimiterInterface {
    private final int maxRequests; // 时间窗口内最大请求数
    private final long windowSizeInMillis; // 时间窗口大小（毫秒）
    private final Queue<Long> requestLogs; // 请求时间戳队列
    private final Lock lock; // 用于线程安全

    public SlidingWindowLogRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
        this.requestLogs = new LinkedList<>();
        this.lock = new ReentrantLock();
    }

    public boolean allowRequest() {
        lock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            long windowStart = currentTime - windowSizeInMillis;

            // 移除过期的请求记录
            while (!requestLogs.isEmpty() && requestLogs.peek() <= windowStart) {
                requestLogs.poll();
            }

            // 检查当前请求是否超过限制
            if (requestLogs.size() < maxRequests) {
                requestLogs.offer(currentTime);
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    public int getCurrentRequests() {
        lock.lock();
        try {
            return requestLogs.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean tryAcquire() throws InterruptedException {
        return allowRequest();
    }
}