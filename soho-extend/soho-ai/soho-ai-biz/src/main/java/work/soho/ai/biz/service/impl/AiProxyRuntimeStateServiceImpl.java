package work.soho.ai.biz.service.impl;

import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiProxyConfig;
import work.soho.ai.biz.service.AiProxyRuntimeStateService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI 代理节点运行时状态服务实现。
 */
@Service
public class AiProxyRuntimeStateServiceImpl implements AiProxyRuntimeStateService {
    private static final long DEFAULT_CIRCUIT_OPEN_MS = 30_000L;
    private static final long DEFAULT_SLOW_CIRCUIT_OPEN_MS = 15_000L;
    private static final int DEFAULT_FAILURE_THRESHOLD = 2;
    private static final int DEFAULT_SLOW_THRESHOLD = 2;
    private static final long DEFAULT_SLOW_TOTAL_MS = 15_000L;
    private static final double EWMA_ALPHA = 0.25D;

    private final Map<Long, ProxyRuntimeState> stateMap = new ConcurrentHashMap<>();

    /**
     * 判断当前代理节点是否允许继续接收请求。
     *
     * @param proxyConfig 代理配置
     * @return 是否允许请求
     */
    @Override
    public boolean isRequestAllowed(AiProxyConfig proxyConfig) {
        if (proxyConfig == null || proxyConfig.getId() == null) {
            return false;
        }
        ProxyRuntimeState state = stateMap.get(proxyConfig.getId());
        return state == null || state.openUntilMs <= System.currentTimeMillis();
    }

    /**
     * 计算代理节点当前有效权重。
     *
     * @param proxyConfig 代理配置
     * @return 动态有效权重
     */
    @Override
    public int getEffectiveWeight(AiProxyConfig proxyConfig) {
        if (proxyConfig == null || proxyConfig.getId() == null || !isRequestAllowed(proxyConfig)) {
            return 0;
        }
        int baseWeight = normalizeWeight(proxyConfig.getWeight());
        ProxyRuntimeState state = stateMap.get(proxyConfig.getId());
        if (state == null) {
            return baseWeight;
        }
        double factor = 1D;
        if (state.ewmaTotalMs > DEFAULT_SLOW_TOTAL_MS) {
            factor *= Math.max(0.2D, (double) DEFAULT_SLOW_TOTAL_MS / (double) state.ewmaTotalMs);
        }
        if (state.consecutiveFailures > 0) {
            factor *= 0.5D;
        }
        int adjustedWeight = (int) Math.round(baseWeight * factor);
        return Math.max(adjustedWeight, baseWeight > 0 ? 1 : 0);
    }

    /**
     * 记录一次代理请求成功。
     *
     * @param proxyConfigId 代理配置ID
     * @param totalMs       总耗时
     */
    @Override
    public void recordSuccess(Long proxyConfigId, long totalMs) {
        if (proxyConfigId == null) {
            return;
        }
        ProxyRuntimeState state = stateMap.computeIfAbsent(proxyConfigId, key -> new ProxyRuntimeState());
        synchronized (state) {
            state.lastSuccessAtMs = System.currentTimeMillis();
            state.consecutiveFailures = 0;
            state.lastErrorMessage = null;
            state.ewmaTotalMs = updateEwma(state.ewmaTotalMs, totalMs);
            if (totalMs >= DEFAULT_SLOW_TOTAL_MS) {
                state.consecutiveSlowRequests++;
                if (state.consecutiveSlowRequests >= DEFAULT_SLOW_THRESHOLD) {
                    state.openUntilMs = System.currentTimeMillis() + DEFAULT_SLOW_CIRCUIT_OPEN_MS;
                    state.consecutiveSlowRequests = 0;
                }
            } else {
                state.consecutiveSlowRequests = 0;
                state.openUntilMs = 0L;
            }
        }
    }

    /**
     * 记录一次代理请求失败。
     *
     * @param proxyConfigId 代理配置ID
     * @param throwable     异常
     */
    @Override
    public void recordFailure(Long proxyConfigId, Throwable throwable) {
        if (proxyConfigId == null || !isProxyRelevantFailure(throwable)) {
            return;
        }
        ProxyRuntimeState state = stateMap.computeIfAbsent(proxyConfigId, key -> new ProxyRuntimeState());
        synchronized (state) {
            state.lastFailureAtMs = System.currentTimeMillis();
            state.consecutiveFailures++;
            state.consecutiveSlowRequests = 0;
            state.lastErrorMessage = throwable == null ? null : throwable.getMessage();
            if (state.consecutiveFailures >= DEFAULT_FAILURE_THRESHOLD || isTimeoutLike(throwable)) {
                state.openUntilMs = System.currentTimeMillis() + DEFAULT_CIRCUIT_OPEN_MS;
            }
        }
    }

    /**
     * 清理指定代理节点运行时状态。
     *
     * @param proxyConfigId 代理配置ID
     */
    @Override
    public void clearState(Long proxyConfigId) {
        if (proxyConfigId == null) {
            return;
        }
        stateMap.remove(proxyConfigId);
    }

    /**
     * 更新 EWMA 延迟值。
     *
     * @param current 当前值
     * @param sample  新样本
     * @return 更新后的结果
     */
    private long updateEwma(long current, long sample) {
        if (sample <= 0) {
            return current;
        }
        if (current <= 0) {
            return sample;
        }
        return Math.round(current * (1D - EWMA_ALPHA) + sample * EWMA_ALPHA);
    }

    /**
     * 判断是否属于代理相关错误。
     *
     * @param throwable 异常
     * @return 是否计入代理失败
     */
    private boolean isProxyRelevantFailure(Throwable throwable) {
        if (throwable == null || throwable.getMessage() == null) {
            return false;
        }
        String message = throwable.getMessage().toLowerCase();
        return message.contains("proxy")
                || message.contains("socks")
                || message.contains("relay")
                || message.contains("connect")
                || message.contains("connection reset")
                || message.contains("connection refused")
                || message.contains("no route to host")
                || message.contains("broken pipe")
                || message.contains("unresolved")
                || message.contains("unknown host")
                || message.contains("dns")
                || isTimeoutLike(throwable);
    }

    /**
     * 判断是否为超时类异常。
     *
     * @param throwable 异常
     * @return 是否为超时类异常
     */
    private boolean isTimeoutLike(Throwable throwable) {
        if (throwable == null || throwable.getMessage() == null) {
            return false;
        }
        String message = throwable.getMessage().toLowerCase();
        return message.contains("timeout")
                || message.contains("timed out")
                || message.contains("read timed out")
                || message.contains("first token timeout");
    }

    /**
     * 规范化基础权重。
     *
     * @param weight 配置权重
     * @return 权重值
     */
    private int normalizeWeight(Integer weight) {
        if (weight == null) {
            return 1;
        }
        return Math.max(weight, 0);
    }

    /**
     * 代理节点运行时状态。
     */
    private static final class ProxyRuntimeState {
        private long openUntilMs;
        private long lastSuccessAtMs;
        private long lastFailureAtMs;
        private long ewmaTotalMs;
        private int consecutiveFailures;
        private int consecutiveSlowRequests;
        private String lastErrorMessage;
    }
}
