package work.soho.ai.biz.dto;

import lombok.Data;

/**
 * AI 代理节点运行时状态快照。
 */
@Data
public class AiProxyRuntimeStateSnapshot {
    /**
     * 代理配置ID。
     */
    private Long proxyConfigId;

    /**
     * 基础权重。
     */
    private Integer baseWeight;

    /**
     * 动态有效权重。
     */
    private Integer effectiveWeight;

    /**
     * 当前是否允许接收请求。
     */
    private Boolean requestAllowed;

    /**
     * 当前是否已熔断。
     */
    private Boolean circuitOpen;

    /**
     * 熔断截止时间戳。
     */
    private Long circuitOpenUntilMs;

    /**
     * 最近成功时间戳。
     */
    private Long lastSuccessAtMs;

    /**
     * 最近失败时间戳。
     */
    private Long lastFailureAtMs;

    /**
     * EWMA 总耗时。
     */
    private Long ewmaTotalMs;

    /**
     * 连续失败次数。
     */
    private Integer consecutiveFailures;

    /**
     * 连续慢请求次数。
     */
    private Integer consecutiveSlowRequests;

    /**
     * 最近失败原因。
     */
    private String lastErrorMessage;

    /**
     * 累计成功次数。
     */
    private Long totalSuccessCount;

    /**
     * 累计失败次数。
     */
    private Long totalFailureCount;
}
