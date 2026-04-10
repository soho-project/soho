package work.soho.ai.biz.dto;

import lombok.Data;
import work.soho.ai.biz.request.AiChatRequest;

/**
 * 提示词渲染结果。
 */
@Data
public class AiPromptRenderResult {
    private Long templateId;
    private String templateCode;
    private Integer templateVersion;
    private String sceneCode;
    private String renderedInstructions;
    private String renderedInput;
    private AiChatRequest renderedRequest;
}
