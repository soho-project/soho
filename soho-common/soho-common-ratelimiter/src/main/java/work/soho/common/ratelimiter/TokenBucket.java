package work.soho.common.ratelimiter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 令牌桶算法实现
 * 用于限流控制，确保系统在单位时间内处理的请求不超过指定数量
 */
public class TokenBucket implements RateLimiterInterface {

    // 桶的容量，即最大令牌数量
    private final long capacity;

    // 令牌生成速率（每秒生成的令牌数）
    private final long refillRate;

    // 当前桶中的令牌数量
    private AtomicLong currentTokens;

    // 上次补充令牌的时间戳（纳秒）
    private AtomicLong lastRefillTime;

    // 用于同步的锁
    private final Lock lock = new ReentrantLock();

    /**
     * 构造函数
     * @param capacity 桶容量（最大令牌数）
     * @param refillRate 令牌生成速率（令牌/秒）
     */
    public TokenBucket(long capacity, long refillRate) {
        if (capacity <= 0 || refillRate <= 0) {
            throw new IllegalArgumentException("容量和速率必须大于0");
        }

        this.capacity = capacity;
        this.refillRate = refillRate;
        this.currentTokens = new AtomicLong(capacity); // 初始时桶是满的
        this.lastRefillTime = new AtomicLong(System.nanoTime());
    }

    /**
     * 尝试获取一个令牌
     * @return 如果获取成功返回true，否则返回false
     */
    public boolean tryAcquire() {
        return tryAcquire(1);
    }

    /**
     * 尝试获取指定数量的令牌
     * @param tokens 请求的令牌数量
     * @return 如果获取成功返回true，否则返回false
     */
    public boolean tryAcquire(long tokens) {
        if (tokens <= 0) {
            throw new IllegalArgumentException("请求的令牌数必须大于0");
        }
        if (tokens > capacity) {
            return false; // 请求的令牌数超过桶容量，直接拒绝
        }

        lock.lock();
        try {
            // 补充令牌
            refillTokens();

            // 检查是否有足够的令牌
            if (currentTokens.get() >= tokens) {
                currentTokens.addAndGet(-tokens);
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 阻塞方式获取一个令牌
     * @throws InterruptedException 如果线程被中断
     */
    public void acquire() throws InterruptedException {
        acquire(1);
    }

    /**
     * 阻塞方式获取指定数量的令牌
     * @param tokens 请求的令牌数量
     * @throws InterruptedException 如果线程被中断
     */
    public void acquire(long tokens) throws InterruptedException {
        while (!tryAcquire(tokens)) {
            // 计算需要等待的时间
            long waitTime = calculateWaitTime(tokens);
            if (waitTime > 0) {
                TimeUnit.NANOSECONDS.sleep(waitTime);
            }
        }
    }

    /**
     * 计算获取指定数量令牌需要等待的时间
     * @param tokens 请求的令牌数量
     * @return 需要等待的纳秒数
     */
    private long calculateWaitTime(long tokens) {
        lock.lock();
        try {
            refillTokens();

            if (currentTokens.get() >= tokens) {
                return 0; // 不需要等待
            }

            // 计算需要生成的令牌数
            long tokensNeeded = tokens - currentTokens.get();

            // 计算生成这些令牌需要的时间（纳秒）
            return (long) ((tokensNeeded / (double) refillRate) * 1_000_000_000);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 补充令牌（基于时间差计算应该补充的令牌数）
     */
    private void refillTokens() {
        long now = System.nanoTime();
        long lastTime = lastRefillTime.get();

        // 计算时间差（秒）
        double elapsedTime = (now - lastTime) / 1_000_000_000.0;

        if (elapsedTime > 0) {
            // 计算应该补充的令牌数量
            long tokensToAdd = (long) (elapsedTime * refillRate);

            if (tokensToAdd > 0) {
                // 更新最后补充时间
                lastRefillTime.set(now);

                // 补充令牌，但不能超过容量
                long newTokens = Math.min(currentTokens.get() + tokensToAdd, capacity);
                currentTokens.set(newTokens);
            }
        }
    }

    /**
     * 获取当前桶中的令牌数量
     * @return 当前令牌数量
     */
    public long getCurrentTokens() {
        lock.lock();
        try {
            refillTokens();
            return currentTokens.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        // 创建一个容量为10，每秒生成2个令牌的桶
        TokenBucket bucket = new TokenBucket(10, 2);

        // 测试非阻塞获取
        System.out.println("测试非阻塞获取:");
        for (int i = 0; i < 15; i++) {
            boolean acquired = bucket.tryAcquire();
            System.out.println("请求 " + (i + 1) + ": " + (acquired ? "成功" : "失败") +
                    ", 剩余令牌: " + bucket.getCurrentTokens());

            if (i % 5 == 4) {
                try {
                    Thread.sleep(1000); // 等待1秒让令牌补充
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // 测试阻塞获取
        System.out.println("\n测试阻塞获取:");
        Thread thread = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    bucket.acquire();
                    System.out.println("获取成功 " + (i + 1) + ", 时间: " + System.currentTimeMillis());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}