package work.soho.content.biz.wordpress;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * WordPress 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "soho.content.wordpress")
public class WordPressProperties {
    private boolean enabled = false;
    private String baseUrl;
    private String username;
    private String appPassword;
}
