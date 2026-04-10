package work.soho.ai.biz.service.impl;

import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiProviderRuntimeStateSnapshotDTO;
import work.soho.ai.biz.service.AiProviderRuntimeStateService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI 提供方运行时状态服务实现。
 */
@Service
public class AiProviderRuntimeStateServiceImpl implements AiProviderRuntimeStateService {
    private static final long DEFAULT_CIRCUIT_OPEN_MS = 30_000L;
    private static final long DEFAULT_SLOW_CIRCUIT_OPEN_MS = 15_000L;
    private static final int DEFAULT_FAILURE_THRESHOLD = 2;
    private static final int DEFAULT_SLOW_THRESHOLD = 2;
    private static final long DEFAULT_SLOW_TOTAL_MS = 15_000L;
    private static final long DEFAULT_SLOW_FIRST_TOKEN_MS = 8_000L;
    private static final double EWMA_ALPHA = 0.25D;

    private final Map<Long, ProviderRuntimeState> stateMap = new ConcurrentHashMap<>();

    /**
     * 判断当前提供方是否允许继续接收请求。
     *
     * @param providerConfig 提供方配置
     * @return 是否允许请求
     */
    @Override
    public boolean isRequestAllowed(AiProviderConfig providerConfig) {
        if (providerConfig == null || providerConfig.getId() == null) {
            return false;
        }
        ProviderRuntimeState state = stateMap.get(providerConfig.getId());
        return state == null || state.openUntilMs <= System.currentTimeMillis();
    }

    /**
     * 计算运行时动态权重。
     *
     * @param providerConfig 提供方配置
     * @return 动态权重
     */
    @Override
    public int getEffectiveWeight(AiProviderConfig providerConfig) {
        if (providerConfig == null || providerConfig.getId() == null || !isRequestAllowed(providerConfig)) {
            return 0;
        }
        int baseWeight = normalizeWeight(providerConfig.getWeight());
        ProviderRuntimeState state = stateMap.get(providerConfig.getId());
        if (state == null) {
            return baseWeight;
        }

        double factor = 1D;
        if (state.ewmaFirstTokenMs > DEFAULT_SLOW_FIRST_TOKEN_MS) {
            factor *= Math.max(0.2D, (double) DEFAULT_SLOW_FIRST_TOKEN_MS / (double) state.ewmaFirstTokenMs);
        }
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
     * 记录一次上游成功。
     *
     * @param providerConfig 提供方配置
     * @param totalMs 总耗时
     * @param firstTokenMs 首字耗时
     */
    @Override
    public void recordSuccess(AiProviderConfig providerConfig, long totalMs, Long firstTokenMs) {
        if (providerConfig == null || providerConfig.getId() == null) {
            return;
        }
        ProviderRuntimeState state = stateMap.computeIfAbsent(providerConfig.getId(), key -> new ProviderRuntimeState());
        synchronized (state) {
            state.lastSuccessAtMs = System.currentTimeMillis();
            state.consecutiveFailures = 0;
            state.lastErrorMessage = null;
            state.ewmaTotalMs = updateEwma(state.ewmaTotalMs, totalMs);
            if (firstTokenMs != null && firstTokenMs > 0) {
                state.ewmaFirstTokenMs = updateEwma(state.ewmaFirstTokenMs, firstTokenMs);
            }
            boolean isSlow = totalMs >= DEFAULT_SLOW_TOTAL_MS
                    || (firstTokenMs != null && firstTokenMs >= DEFAULT_SLOW_FIRST_TOKEN_MS);
            if (isSlow) {
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
     * 记录一次上游失败。
     *
     * @param providerConfig 提供方配置
     * @param throwable 异常
     */
    @Override
    public void recordFailure(AiProviderConfig providerConfig, Throwable throwable) {
        if (providerConfig == null || providerConfig.getId() == null) {
            return;
        }
        ProviderRuntimeState state = stateMap.computeIfAbsent(providerConfig.getId(), key -> new ProviderRuntimeState());
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
     * 读取当前提供方运行时快照。
     *
     * @param providerConfig 提供方配置
     * @return 运行时快照
     */
    @Override
    public AiProviderRuntimeStateSnapshotDTO getSnapshot(AiProviderConfig providerConfig) {
        AiProviderRuntimeStateSnapshotDTO snapshot = new AiProviderRuntimeStateSnapshotDTO();
        if (providerConfig == null || providerConfig.getId() == null) {
            snapshot.setRequestAllowed(false);
            snapshot.setEffectiveWeight(0);
            snapshot.setConsecutiveFailures(0);
            snapshot.setConsecutiveSlowRequests(0);
            return snapshot;
        }
        snapshot.setProviderConfigId(providerConfig.getId());
        snapshot.setRequestAllowed(isRequestAllowed(providerConfig));
        snapshot.setEffectiveWeight(getEffectiveWeight(providerConfig));
        ProviderRuntimeState state = stateMap.get(providerConfig.getId());
        if (state == null) {
            snapshot.setConsecutiveFailures(0);
            snapshot.setConsecutiveSlowRequests(0);
            return snapshot;
        }
        synchronized (state) {
            snapshot.setCircuitOpenUntilMs(state.openUntilMs > 0 ? state.openUntilMs : null);
            snapshot.setLastSuccessAtMs(state.lastSuccessAtMs > 0 ? state.lastSuccessAtMs : null);
            snapshot.setLastFailureAtMs(state.lastFailureAtMs > 0 ? state.lastFailureAtMs : null);
            snapshot.setEwmaTotalMs(state.ewmaTotalMs > 0 ? state.ewmaTotalMs : null);
            snapshot.setEwmaFirstTokenMs(state.ewmaFirstTokenMs > 0 ? state.ewmaFirstTokenMs : null);
            snapshot.setConsecutiveFailures(state.consecutiveFailures);
            snapshot.setConsecutiveSlowRequests(state.consecutiveSlowRequests);
            snapshot.setLastErrorMessage(state.lastErrorMessage);
        }
        return snapshot;
    }

    /**
     * 清理指定提供方的运行时状态。
     *
     * @param providerConfigId 提供方配置ID
     */
    @Override
    public void clearState(Long providerConfigId) {
        if (providerConfigId == null) {
            return;
        }
        stateMap.remove(providerConfigId);
    }

    /**
     * 更新 EWMA 延迟值。
     *
     * @param current 当前值
     * @param sample 新样本
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
     * 提供方运行时状态。
     */
    private static final class ProviderRuntimeState {
        private long openUntilMs;
        private long lastSuccessAtMs;
        private long lastFailureAtMs;
        private long ewmaTotalMs;
        private long ewmaFirstTokenMs;
        private int consecutiveFailures;
        private int consecutiveSlowRequests;
        private String lastErrorMessage;
    }
}
