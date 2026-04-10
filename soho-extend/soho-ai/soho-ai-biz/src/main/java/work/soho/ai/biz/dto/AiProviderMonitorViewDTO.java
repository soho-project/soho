package work.soho.ai.biz.dto;

import lombok.Data;

/**
 * AI 提供方监控视图。
 */
@Data
public class AiProviderMonitorViewDTO {
    private Long providerConfigId;
    private String providerCode;
    private String provider;
    private String env;
    private String defaultModel;
    private Integer status;
    private Integer baseWeight;
    private Integer effectiveWeight;
    private boolean requestAllowed;
    private Long circuitOpenUntilMs;
    private Long lastSuccessAtMs;
    private Long lastFailureAtMs;
    private Long ewmaTotalMs;
    private Long ewmaFirstTokenMs;
    private Integer consecutiveFailures;
    private Integer consecutiveSlowRequests;
    private String lastErrorMessage;
    private Long todayRequestCount;
    private Long todayPromptTokens;
    private Long todayCompletionTokens;
    private Long todayTotalTokens;
    private Long totalRequestCount;
    private Long totalPromptTokens;
    private Long totalCompletionTokens;
    private Long totalTotalTokens;
}
