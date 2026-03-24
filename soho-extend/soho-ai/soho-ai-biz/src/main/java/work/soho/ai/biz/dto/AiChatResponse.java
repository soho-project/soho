package work.soho.ai.biz.dto;

import lombok.Data;

@Data
public class AiChatResponse {
    private String provider;
    private String model;
    private String content;
    private String raw;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
}
