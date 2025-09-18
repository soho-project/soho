package work.soho.common.ratelimiter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 基于时间片的滑动窗口限流器（更精确的实现）
 */
public class AdvancedSlidingWindowRateLimiter implements RateLimiterInterface {
    // 时间窗口内允许的最大请求数
    private final int maxRequests;
    // 时间窗口大小（毫秒）
    private final long windowSizeInMillis;
    // 时间片数量
    private final int sliceNumber;
    // 每个时间片的大小（毫秒）
    private final long sliceSizeInMillis;
    // 时间片计数器数组
    private final AtomicInteger[] slices;
    // 时间片开始时间数组
    private final AtomicLong[] sliceStartTimes;
    // 当前时间片索引
    private final AtomicInteger currentIndex;

    /**
     * 构造函数
     * @param maxRequests 时间窗口内允许的最大请求数
     * @param windowSize 时间窗口大小
     * @param timeUnit 时间单位
     * @param sliceNumber 时间片数量（建议10-20）
     */
    public AdvancedSlidingWindowRateLimiter(int maxRequests, long windowSize,
                                            TimeUnit timeUnit, int sliceNumber) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = timeUnit.toMillis(windowSize);
        this.sliceNumber = sliceNumber;
        this.sliceSizeInMillis = windowSizeInMillis / sliceNumber;

        this.slices = new AtomicInteger[sliceNumber];
        this.sliceStartTimes = new AtomicLong[sliceNumber];
        for (int i = 0; i < sliceNumber; i++) {
            slices[i] = new AtomicInteger(0);
            sliceStartTimes[i] = new AtomicLong(0);
        }

        this.currentIndex = new AtomicInteger(0);
    }

    /**
     * 尝试获取请求许可
     * @return true-允许请求，false-拒绝请求
     */
    public boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        int index = currentIndex.get();

        // 检查当前时间片是否需要重置
        if (currentTime - sliceStartTimes[index].get() > sliceSizeInMillis) {
            // 使用CAS操作确保只有一个线程重置时间片
            if (sliceStartTimes[index].compareAndSet(
                    sliceStartTimes[index].get(), currentTime)) {
                slices[index].set(0);
                currentIndex.set((index + 1) % sliceNumber);
            }
        }

        // 计算当前窗口内的总请求数
        int totalRequests = 0;
        for (int i = 0; i < sliceNumber; i++) {
            // 只统计在时间窗口内的切片
            if (currentTime - sliceStartTimes[i].get() < windowSizeInMillis) {
                totalRequests += slices[i].get();
            }
        }

        // 检查是否超过限制
        if (totalRequests < maxRequests) {
            slices[index].incrementAndGet();
            return true;
        }

        return false;
    }
}