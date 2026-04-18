package work.soho.ai.biz.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiUserApiKeyView {
    private Long id;
    private String name;
    private String apiKeyPrefix;
    private Integer status;
    private LocalDateTime expireEndTime;
    private LocalDateTime lastUsedTime;
    private LocalDateTime createdTime;
}
