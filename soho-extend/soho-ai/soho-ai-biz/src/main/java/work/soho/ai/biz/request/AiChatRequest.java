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
     * 提示词场景编码
     */
    private String sceneCode;

    /**
     * 指定提示词模板编码
     */
    private String templateCode;

    /**
     * 提示词模板变量
     */
    private Map<String, Object> promptVars;

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
            this.imageUrls = null;
            this.fileUrls = null;
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
                String type = map.get("type") == null ? "" : String.valueOf(map.get("type")).toLowerCase();
                String text = extractText(map, type);
                if (StringUtils.isNotBlank(text)) {
                    if (textBuilder.length() > 0) {
                        textBuilder.append("\n");
                    }
                    textBuilder.append(text);
                }

                String imageUrl = extractImageUrl(map, type);
                if (StringUtils.isNotBlank(imageUrl)) {
                    appendUnique(parsedImageUrls, imageUrl);
                }

                String fileUrl = extractFileUrl(map, type);
                if (StringUtils.isNotBlank(fileUrl)) {
                    appendUnique(parsedFileUrls, fileUrl);
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

        private String extractText(Map<?, ?> map, String type) {
            if ("text".equals(type) || "input_text".equals(type) || "output_text".equals(type)) {
                Object textValue = map.get("text");
                if (textValue == null) {
                    textValue = map.get("input_text");
                }
                if (textValue != null) {
                    return String.valueOf(textValue);
                }
            }
            return null;
        }

        private String extractImageUrl(Map<?, ?> map, String type) {
            if ("image_url".equals(type)) {
                return extractUrl(map.get("image_url"));
            }
            if ("input_image".equals(type) || "image".equals(type)) {
                String url = extractUrl(map.get("image_url"));
                if (StringUtils.isNotBlank(url)) {
                    return url;
                }
                url = extractUrl(map.get("url"));
                if (StringUtils.isNotBlank(url)) {
                    return url;
                }
                url = extractUrl(map.get("image"));
                if (StringUtils.isNotBlank(url)) {
                    return url;
                }
                return extractUrl(map.get("input_image"));
            }
            return null;
        }

        private String extractFileUrl(Map<?, ?> map, String type) {
            if ("file_url".equals(type)) {
                return extractUrl(map.get("file_url"));
            }
            if ("input_file".equals(type) || "file".equals(type)) {
                String url = extractUrl(map.get("file_url"));
                if (StringUtils.isNotBlank(url)) {
                    return url;
                }
                url = extractUrl(map.get("url"));
                if (StringUtils.isNotBlank(url)) {
                    return url;
                }
                url = extractUrl(map.get("file"));
                if (StringUtils.isNotBlank(url)) {
                    return url;
                }
                return extractUrl(map.get("input_file"));
            }
            return null;
        }

        private void appendUnique(List<String> list, String value) {
            if (!list.contains(value)) {
                list.add(value);
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
