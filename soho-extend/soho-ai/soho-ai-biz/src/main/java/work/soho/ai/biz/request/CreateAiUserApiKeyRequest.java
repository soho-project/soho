package work.soho.ai.biz.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAiUserApiKeyRequest {
    private String name;
    private LocalDateTime expireEndTime;
}
