package work.soho.ai.biz.dto;

import lombok.Data;

@Data
public class AiUserApiKeyCreatedResponse {
    private Long id;
    private String name;
    private String apiKey;
    private String apiKeyPrefix;
}
