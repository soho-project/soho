package work.soho.admin.biz.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({DeepSeekProperties.class, GeminiProperties.class})
public class AiConfig {}
