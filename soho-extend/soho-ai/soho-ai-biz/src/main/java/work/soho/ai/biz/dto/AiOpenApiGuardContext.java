package work.soho.ai.biz.dto;

import lombok.Data;
import work.soho.ai.biz.domain.AiUserApiKey;

@Data
public class AiOpenApiGuardContext {
    private String requestId;

    private String endpoint;

    private String requestSource;

    private String clientIp;

    private String userAgent;

    private AiUserApiKey apiKey;
}
