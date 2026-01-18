# 限流器 (Rate Limiter)

## 简介

本项目提供了一系列基于不同算法的限流器（Rate Limiter）实现，位于 `soho-common-ratelimiter` 模块中。

这些限流器是**编程式**的，意味着你需要手动在代码中实例化和调用它们，项目**未提供**基于注解（Annotation）的 AOP 自动集成。这种方式提供了最大的灵活性，可以方便地在业务代码的任何位置进行精细的流量控制。

##核心接口：`RateLimiterInterface`

所有的限流器都实现了 `RateLimiterInterface` 接口，该接口定义了唯一的限流方法：

```java
public interface RateLimiterInterface {
    /**
     * 尝试获取一个执行许可
     * @return 如果获取成功返回 true，否则返回 false
     */
    boolean tryAcquire() throws InterruptedException;
}
```

返回 `true` 代表在限流策略允许的范围内，请求可以继续执行。返回 `false` 则代表请求已被限流。

## 通用使用模式

在你的 Service、Controller 或其他需要限流的组件中，遵循以下三步即可使用：

1.  **实例化限流器**：在类中根据需求选择一个具体的限流器实现，并初始化它。
2.  **调用 `tryAcquire()`**：在执行核心业务逻辑前，调用限流器的 `tryAcquire()` 方法。
3.  **处理结果**：根据 `tryAcquire()` 的返回结果，决定是继续执行业务逻辑，还是拒绝请求（例如：直接返回、抛出特定异常）。

```java
@Service
public class MyService {

    // 1. 实例化一个限流器。
    // 此处使用固定窗口算法，配置为：每分钟最多允许 100 次请求。
    private final RateLimiterInterface rateLimiter = new FixedWindowRateLimiter(100, 60 * 1000);

    /**
     * 一个需要被限流的方法
     */
    public void myRateLimitedMethod() {
        try {
            // 2. 尝试获取许可
            if (rateLimiter.tryAcquire()) {
                // 获取成功，执行核心业务逻辑
                System.out.println("业务逻辑正在执行...");
                // ...
            } else {
                // 3. 获取失败，拒绝请求
                System.out.println("请求过于频繁，请稍后再试。");
                throw new RuntimeException("Too many requests!");
            }
        } catch (InterruptedException e) {
            // 建议处理中断异常
            Thread.currentThread().interrupt();
        }
    }
}
```

## 可用的算法实现

你可以根据不同的业务场景，选择合适的限流算法。

---

### 1. 固定窗口算法 (`FixedWindowRateLimiter`)

这是最简单的限流算法。它在固定的时间窗口内（如 1 秒）允许固定数量的请求。如果当前窗口的请求数超限，新请求将被拒绝。

-   **优点**：实现简单，容易理解。
-   **缺点**：在时间窗口的边界处可能出现流量突刺（例如，窗口切换的瞬间，流量可以达到限制的2倍）。
-   **线程安全**：非线程安全，适用于单线程环境。

**构造方法**：
```java
public FixedWindowRateLimiter(int maxRequestCount, long windowSizeInMillis)
```
-   `maxRequestCount`: 窗口内的最大请求数。
-   `windowSizeInMillis`: 时间窗口的大小（毫秒）。

---

### 2. 同步固定窗口算法 (`SynchronizedFixedWindowRateLimiter`)

与 `FixedWindowRateLimiter` 逻辑相同，但通过 `synchronized` 关键字保证了线程安全，适用于多线程环境。

-   **线程安全**：是。

**构造方法**：
```java
public SynchronizedFixedWindowRateLimiter(int maxRequestCount, long windowSizeInMillis)
```

---

### 3. 滑动窗口算法 (`SlidingWindowRateLimiter`)

为了解决固定窗口的临界突刺问题，滑动窗口将时间窗口划分为更小的格子。每次请求过来，都会落在当前格子里，并且算法会统计一个滑动窗口（包含多个格子）的总请求数。

-   **优点**：限流控制更为平滑，有效避免了边界突刺。
-   **此版本实现**：基于 `ZSET` 的思想，使用 `TreeMap` 存储请求时间戳和计数。

**构造方法**：
```java
public SlidingWindowRateLimiter(int maxRequestCount, long windowSizeInMillis)
```

---

### 4. 令牌桶算法 (`TokenBucket`)

这是业界非常流行的一种限流算法。系统以恒定的速率向桶里放入令牌，当请求到来时，需要从桶里获取一个令牌才能执行。如果桶里没有令牌，请求将被拒绝或等待。

-   **优点**：可以应对一定程度的突发流量（桶内积攒的令牌可以被瞬间消耗）。
-   **线程安全**：是，内部使用 `ReentrantLock` 保证。

**构造方法**：
```java
public TokenBucket(long capacity, long refillRate)
```
-   `capacity`: 令牌桶的容量。
-   `refillRate`: 令牌生成速率（个/秒）。

---

### 其他算法

项目中还包含了其他几种算法的实现，可供研究或在特定场景下使用：

-   `AdvancedSlidingWindowRateLimiter`: 基于时间片的滑动窗口限流器。
-   `SlidingWindowLogRateLimiter`: 滑动窗口日志限流算法。
-   `BlockingLeakyBucketRateLimiter`: 阻塞式漏桶限流算法，请求会排队等待处理。
