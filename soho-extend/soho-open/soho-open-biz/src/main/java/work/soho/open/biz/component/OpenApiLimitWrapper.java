package work.soho.open.biz.component;

import work.soho.common.ratelimiter.SlidingWindowRateLimiter;

import java.util.concurrent.TimeUnit;

public class OpenApiLimitWrapper extends SlidingWindowRateLimiter {
    private int max;

    volatile long lastAccessTime;

    public OpenApiLimitWrapper(int maxRequests, long windowSize, TimeUnit timeUnit) {
        super(maxRequests, windowSize, timeUnit);
        this.max = maxRequests;
    }

    /**
     * 最大请求数
     * @return
     */
    public int getMax() {
        return max;
    }

    void touch() {
        this.lastAccessTime = System.currentTimeMillis();
    }

    void destroy() {
//        super.destroy();
    }
}
