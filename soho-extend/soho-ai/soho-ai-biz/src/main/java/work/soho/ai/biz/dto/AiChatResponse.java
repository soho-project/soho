package work.soho.ai.biz.dto;

import lombok.Data;

@Data
public class AiChatResponse {
    private Long providerConfigId;
    private String providerCode;
    private String provider;
    private String model;
    private String requestModel;
    private String actualModel;
    private String content;
    private String raw;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private Integer cachedInputTokens;
    private Integer cacheCreationInputTokens;
    private Integer cacheReadInputTokens;
}
