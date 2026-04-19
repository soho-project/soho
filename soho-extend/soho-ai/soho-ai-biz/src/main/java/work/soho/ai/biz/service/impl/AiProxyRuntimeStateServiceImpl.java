package work.soho.ai.biz.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiProxyConfig;
import work.soho.ai.biz.dto.AiProxyRuntimeStateSnapshot;
import work.soho.ai.biz.service.AiProxyRuntimeStateRepository;
import work.soho.ai.biz.service.AiProxyRuntimeStateService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    private final AiProxyRuntimeStateRepository runtimeStateRepository;

    /**
     * 创建默认运行时状态服务实例。
     */
    public AiProxyRuntimeStateServiceImpl() {
        this(new InMemoryRepository());
    }

    /**
     * 创建带仓储的运行时状态服务实例。
     *
     * @param runtimeStateRepository 运行时状态仓储
     */
    @Autowired
    public AiProxyRuntimeStateServiceImpl(AiProxyRuntimeStateRepository runtimeStateRepository) {
        this.runtimeStateRepository = runtimeStateRepository == null ? new InMemoryRepository() : runtimeStateRepository;
    }

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
        ProxyRuntimeState state = getOrLoadState(proxyConfig.getId());
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
        ProxyRuntimeState state = getOrLoadState(proxyConfig.getId());
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
            state.totalSuccessCount++;
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
        persistSnapshot(proxyConfigId, null);
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
            state.totalFailureCount++;
        }
        persistSnapshot(proxyConfigId, null);
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
        runtimeStateRepository.delete(proxyConfigId);
    }

    /**
     * 查询指定代理节点运行时状态快照。
     *
     * @param proxyConfig 代理配置
     * @return 运行时状态快照
     */
    @Override
    public AiProxyRuntimeStateSnapshot getStateSnapshot(AiProxyConfig proxyConfig) {
        if (proxyConfig == null || proxyConfig.getId() == null) {
            return null;
        }
        ProxyRuntimeState state = getOrLoadState(proxyConfig.getId());
        return buildSnapshot(proxyConfig, state);
    }

    /**
     * 批量查询代理节点运行时状态快照。
     *
     * @param proxyConfigs 代理配置集合
     * @return 运行时状态映射
     */
    @Override
    public Map<Long, AiProxyRuntimeStateSnapshot> getStateSnapshotMap(Collection<AiProxyConfig> proxyConfigs) {
        if (proxyConfigs == null || proxyConfigs.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, AiProxyRuntimeStateSnapshot> result = new HashMap<>();
        for (AiProxyConfig proxyConfig : proxyConfigs) {
            if (proxyConfig == null || proxyConfig.getId() == null) {
                continue;
            }
            result.put(proxyConfig.getId(), getStateSnapshot(proxyConfig));
        }
        return result;
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
     * 优先从内存读取运行态，未命中时从仓储加载。
     *
     * @param proxyConfigId 代理配置ID
     * @return 运行态
     */
    private ProxyRuntimeState getOrLoadState(Long proxyConfigId) {
        if (proxyConfigId == null) {
            return null;
        }
        ProxyRuntimeState current = stateMap.get(proxyConfigId);
        if (current != null) {
            return current;
        }
        Optional<AiProxyRuntimeStateSnapshot> optional = runtimeStateRepository.findById(proxyConfigId);
        if (optional.isEmpty()) {
            return null;
        }
        ProxyRuntimeState loadedState = fromSnapshot(optional.get());
        ProxyRuntimeState previous = stateMap.putIfAbsent(proxyConfigId, loadedState);
        return previous == null ? loadedState : previous;
    }

    /**
     * 将当前内存运行态同步到仓储。
     *
     * @param proxyConfigId 代理配置ID
     * @param proxyConfig 代理配置
     */
    private void persistSnapshot(Long proxyConfigId, AiProxyConfig proxyConfig) {
        if (proxyConfigId == null) {
            return;
        }
        ProxyRuntimeState state = stateMap.get(proxyConfigId);
        if (state == null) {
            runtimeStateRepository.delete(proxyConfigId);
            return;
        }
        runtimeStateRepository.save(buildSnapshot(proxyConfig, state, proxyConfigId));
    }

    /**
     * 构建运行时状态快照。
     *
     * @param proxyConfig 代理配置
     * @param state 运行态
     * @return 运行时状态快照
     */
    private AiProxyRuntimeStateSnapshot buildSnapshot(AiProxyConfig proxyConfig, ProxyRuntimeState state) {
        Long proxyConfigId = proxyConfig == null ? null : proxyConfig.getId();
        return buildSnapshot(proxyConfig, state, proxyConfigId);
    }

    /**
     * 构建运行时状态快照。
     *
     * @param proxyConfig 代理配置
     * @param state 运行态
     * @param proxyConfigId 代理配置ID
     * @return 运行时状态快照
     */
    private AiProxyRuntimeStateSnapshot buildSnapshot(AiProxyConfig proxyConfig, ProxyRuntimeState state, Long proxyConfigId) {
        AiProxyRuntimeStateSnapshot snapshot = new AiProxyRuntimeStateSnapshot();
        snapshot.setProxyConfigId(proxyConfigId);
        int baseWeight = normalizeWeight(proxyConfig == null ? null : proxyConfig.getWeight());
        snapshot.setBaseWeight(baseWeight);
        if (state == null) {
            snapshot.setEffectiveWeight(baseWeight);
            snapshot.setRequestAllowed(true);
            snapshot.setCircuitOpen(false);
            snapshot.setConsecutiveFailures(0);
            snapshot.setConsecutiveSlowRequests(0);
            snapshot.setTotalSuccessCount(0L);
            snapshot.setTotalFailureCount(0L);
            snapshot.setEwmaTotalMs(0L);
            snapshot.setCircuitOpenUntilMs(0L);
            return snapshot;
        }
        long now = System.currentTimeMillis();
        boolean requestAllowed = state.openUntilMs <= now;
        snapshot.setEffectiveWeight(resolveEffectiveWeight(baseWeight, state, requestAllowed));
        snapshot.setRequestAllowed(requestAllowed);
        snapshot.setCircuitOpen(!requestAllowed);
        snapshot.setCircuitOpenUntilMs(state.openUntilMs);
        snapshot.setLastSuccessAtMs(state.lastSuccessAtMs);
        snapshot.setLastFailureAtMs(state.lastFailureAtMs);
        snapshot.setEwmaTotalMs(state.ewmaTotalMs);
        snapshot.setConsecutiveFailures(state.consecutiveFailures);
        snapshot.setConsecutiveSlowRequests(state.consecutiveSlowRequests);
        snapshot.setLastErrorMessage(state.lastErrorMessage);
        snapshot.setTotalSuccessCount(state.totalSuccessCount);
        snapshot.setTotalFailureCount(state.totalFailureCount);
        return snapshot;
    }

    /**
     * 将仓储快照还原为内存运行态。
     *
     * @param snapshot 仓储快照
     * @return 内存运行态
     */
    private ProxyRuntimeState fromSnapshot(AiProxyRuntimeStateSnapshot snapshot) {
        ProxyRuntimeState state = new ProxyRuntimeState();
        if (snapshot == null) {
            return state;
        }
        state.openUntilMs = defaultLong(snapshot.getCircuitOpenUntilMs());
        state.lastSuccessAtMs = defaultLong(snapshot.getLastSuccessAtMs());
        state.lastFailureAtMs = defaultLong(snapshot.getLastFailureAtMs());
        state.ewmaTotalMs = defaultLong(snapshot.getEwmaTotalMs());
        state.consecutiveFailures = defaultInt(snapshot.getConsecutiveFailures());
        state.consecutiveSlowRequests = defaultInt(snapshot.getConsecutiveSlowRequests());
        state.lastErrorMessage = snapshot.getLastErrorMessage();
        state.totalSuccessCount = defaultLong(snapshot.getTotalSuccessCount());
        state.totalFailureCount = defaultLong(snapshot.getTotalFailureCount());
        return state;
    }

    /**
     * 计算动态有效权重。
     *
     * @param baseWeight 基础权重
     * @param state 运行态
     * @param requestAllowed 当前是否允许请求
     * @return 动态有效权重
     */
    private int resolveEffectiveWeight(int baseWeight, ProxyRuntimeState state, boolean requestAllowed) {
        if (!requestAllowed) {
            return 0;
        }
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
     * 将可空 Long 转为基础值。
     *
     * @param value 原始值
     * @return 基础值
     */
    private long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

    /**
     * 将可空 Integer 转为基础值。
     *
     * @param value 原始值
     * @return 基础值
     */
    private int defaultInt(Integer value) {
        return value == null ? 0 : value;
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
        private long totalSuccessCount;
        private long totalFailureCount;
    }

    /**
     * 默认内存运行时状态仓储。
     */
    private static final class InMemoryRepository implements AiProxyRuntimeStateRepository {
        /**
         * 保存指定代理节点运行时状态。
         *
         * @param snapshot 运行时状态快照
         */
        @Override
        public void save(AiProxyRuntimeStateSnapshot snapshot) {
        }

        /**
         * 读取指定代理节点运行时状态。
         *
         * @param proxyConfigId 代理配置ID
         * @return 运行时状态
         */
        @Override
        public Optional<AiProxyRuntimeStateSnapshot> findById(Long proxyConfigId) {
            return Optional.empty();
        }

        /**
         * 批量读取代理节点运行时状态。
         *
         * @param proxyConfigIds 代理配置ID集合
         * @return 状态映射
         */
        @Override
        public Map<Long, AiProxyRuntimeStateSnapshot> findByIds(Collection<Long> proxyConfigIds) {
            return Collections.emptyMap();
        }

        /**
         * 删除指定代理节点运行时状态。
         *
         * @param proxyConfigId 代理配置ID
         */
        @Override
        public void delete(Long proxyConfigId) {
        }
    }
}
