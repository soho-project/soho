package work.soho.ai.biz.dto;

import lombok.Data;

@Data
public class AiUsageSummary {
    private Integer promptTokens = 0;
    private Integer completionTokens = 0;
    private Integer totalTokens = 0;
}
