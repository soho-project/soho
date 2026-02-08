package work.soho.ai.biz.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AiChatRequest {
    /**
     * AI应用code（ai_app.code）
     */
    private String appCode;

    /**
     * AI提供方配置code（ai_provider_config.code）
     */
    private String providerCode;

    /**
     * 指定模型（优先级高于默认模型）
     */
    private String model;

    /**
     * 单轮输入（当 messages 为空时使用）
     */
    private String input;

    /**
     * 对话消息
     */
    private List<Message> messages;

    private Double temperature;
    private Double topP;
    private Integer maxTokens;
    private Boolean stream;

    /**
     * 透传扩展参数（可选）
     */
    private Map<String, Object> extra;

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
