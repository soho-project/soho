package work.soho.chat.biz.service.impl.ai.adapter;

import lombok.Data;

@Data
public class SparkAiOptions {
    private String hostUrl = "https://spark-api.xf-yun.com/v2.1/chat";
    private String appId;
    private String apiSecret;
    private String apiKey;
}
