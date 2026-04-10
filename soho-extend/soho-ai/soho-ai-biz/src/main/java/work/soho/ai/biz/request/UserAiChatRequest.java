package work.soho.ai.biz.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserAiChatRequest {
    private Long sessionId;
    private String title;
    private String providerCode;
    private String model;
    private String input;
    private List<AiChatRequest.Message> messages;
    private Double temperature;
    private Double topP;
    private Integer maxTokens;
    private Boolean stream;
    private String instructions;
    private String sceneCode;
    private String templateCode;
    private Map<String, Object> promptVars;
    private Map<String, Object> extra;
}
