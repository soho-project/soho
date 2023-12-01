package work.soho.chat.biz.service.impl.ai.adapter;

import lombok.Data;

@Data
public class SohoAiOptions {
    private String model = "gpt-3.5-turbo";
    private String apiPrefix = "http://127.0.0.1:9999";
    private Double temperature = 0.9;
    private Double topP = Double.valueOf(0);
    private Integer maxLength = 0;
    private Boolean stream = false;
}
