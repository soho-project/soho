package work.soho.ai.biz.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiUserApiKeyCreatedResponse {
    private Long id;
    private String name;
    private String apiKey;
    private String apiKeyPrefix;
    private LocalDateTime expireEndTime;
}
