package work.soho.chat.biz.service.impl.ai.adapter.sohoai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CompletionResult {
    private String model;
    private String object;
    private List<Choice> choices;
    @JsonProperty("function_call")
    private Integer created;


    @Data
    public static class Choice {
        private int index;
        @JsonProperty("finish_reason")
        private String finishReason;
        private Message message;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
        private String functionCall;
    }
}
