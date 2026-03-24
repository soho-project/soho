package work.soho.ai.biz.request;

import lombok.Data;

@Data
public class RenameAiChatSessionRequest {
    private Long sessionId;
    private String title;
}
