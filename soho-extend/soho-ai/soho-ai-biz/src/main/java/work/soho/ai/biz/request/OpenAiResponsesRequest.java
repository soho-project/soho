package work.soho.ai.biz.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OpenAiResponsesRequest {
    private String model;
    private String instructions;
    private Object input;
    private List<Map<String, Object>> tools;
    @JsonProperty("tool_choice")
    private String toolChoice;
    @JsonProperty("parallel_tool_calls")
    private Boolean parallelToolCalls;
    private ReasoningConfig reasoning;
    // chat codex 上游接口里面必须为 false
    private Boolean store = false;

    // chat codex 上游接口必须为 true
    private Boolean stream = true;
    private List<String> include;
    @JsonProperty("service_tier")
    private String serviceTier;
    @JsonProperty("prompt_cache_key")
    private String promptCacheKey;
    private TextConfig text;
    private Double temperature;
    @JsonProperty("top_p")
    private Double topP;
    @JsonProperty("max_output_tokens")
    private Integer maxOutputTokens;

    @Data
    public static class ReasoningConfig {
        private String effort;
        private String summary;
    }

    @Data
    public static class TextConfig {
        private String format;
        private String verbosity;
    }
}
