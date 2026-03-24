package work.soho.ai.biz.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import work.soho.common.core.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class AiChatRequest {
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
     * system prompt / instructions
     */
    private String instructions;

    /**
     * 透传扩展参数（可选）
     */
    private Map<String, Object> extra;

    @Data
    public static class Message {
        private String role;
        private String content;
        private List<String> imageUrls;
        private List<String> fileUrls;

        @JsonSetter("content")
        public void setContent(Object rawContent) {
            this.content = null;
            if (rawContent == null) {
                return;
            }
            if (rawContent instanceof String) {
                this.content = (String) rawContent;
                return;
            }
            if (!(rawContent instanceof List)) {
                this.content = String.valueOf(rawContent);
                return;
            }

            StringBuilder textBuilder = new StringBuilder();
            List<String> parsedImageUrls = new ArrayList<>();
            List<String> parsedFileUrls = new ArrayList<>();
            for (Object item : (List<?>) rawContent) {
                Map<?, ?> map = item instanceof Map ? (Map<?, ?>) item : null;
                if (map == null) {
                    continue;
                }
                Object type = map.get("type");
                if ("text".equals(type) && map.get("text") != null) {
                    if (textBuilder.length() > 0) {
                        textBuilder.append("\n");
                    }
                    textBuilder.append(map.get("text"));
                } else if ("image_url".equals(type)) {
                    String imageUrl = extractUrl(map.get("image_url"));
                    if (StringUtils.isNotBlank(imageUrl)) {
                        parsedImageUrls.add(imageUrl);
                    }
                } else if ("file_url".equals(type)) {
                    String fileUrl = extractUrl(map.get("file_url"));
                    if (StringUtils.isNotBlank(fileUrl)) {
                        parsedFileUrls.add(fileUrl);
                    }
                }
            }
            if (textBuilder.length() > 0) {
                this.content = textBuilder.toString();
            }
            if (!parsedImageUrls.isEmpty()) {
                this.imageUrls = parsedImageUrls;
            }
            if (!parsedFileUrls.isEmpty()) {
                this.fileUrls = parsedFileUrls;
            }
        }

        private String extractUrl(Object value) {
            if (value instanceof String) {
                return (String) value;
            }
            if (value instanceof Map) {
                Object url = ((Map<?, ?>) value).get("url");
                return url == null ? null : String.valueOf(url);
            }
            return null;
        }
    }
}
