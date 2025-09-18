package work.soho.common.ratelimiter;

public interface RateLimiterInterface {
    /**
     * 尝试获取令牌
     *
     * @return 是否成功获取令牌
     */
    boolean tryAcquire()  throws InterruptedException;
}
