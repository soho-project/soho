package work.soho.ai.biz.dto;

import lombok.Data;

/**
 * AI 提供方运行时状态快照。
 */
@Data
public class AiProviderRuntimeStateSnapshotDTO {
    private Long providerConfigId;
    private boolean requestAllowed;
    private Integer effectiveWeight;
    private Long circuitOpenUntilMs;
    private Long lastSuccessAtMs;
    private Long lastFailureAtMs;
    private Long ewmaTotalMs;
    private Long ewmaFirstTokenMs;
    private Integer consecutiveFailures;
    private Integer consecutiveSlowRequests;
    private String lastErrorMessage;
}
