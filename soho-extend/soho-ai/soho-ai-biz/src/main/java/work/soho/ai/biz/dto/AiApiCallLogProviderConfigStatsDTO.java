package work.soho.ai.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiApiCallLogProviderConfigStatsDTO {
    private Long providerConfigId;
    private Long requestCount;
    private Long promptTokens;
    private Long completionTokens;
    private Long totalTokens;
}
