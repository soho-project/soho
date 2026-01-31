package work.soho.admin.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "soho.ai.deepseek")
public class DeepSeekProperties {
    private String baseUrl = "https://api.deepseek.com";
    private String apiKey;
    private String model = "deepseek-chat";
    private int connectTimeoutMs = 10000;
    private int readTimeoutMs = 600000;
}