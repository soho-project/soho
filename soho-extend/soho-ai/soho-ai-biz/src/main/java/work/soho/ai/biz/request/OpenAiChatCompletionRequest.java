package work.soho.ai.biz.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenAiChatCompletionRequest {
    private String model;
    private List<Message> messages;
    private Boolean stream;
    private Double temperature;
    @JsonProperty("top_p")
    private Double topP;
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    @Data
    public static class Message {
        private String role;
        private Object content;
    }
}
