package work.soho.admin.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "soho.ai.gemini")
public class GeminiProperties {
    private String baseUrl = "https://generativelanguage.googleapis.com/v1beta";
    private String apiKey;
    private String model = "gemini-2.5-flash";
    private int connectTimeoutMs = 10000;
    private int readTimeoutMs = 600000;
}
