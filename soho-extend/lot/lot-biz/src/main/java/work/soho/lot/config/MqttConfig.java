package work.soho.lot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource(value = {"classpath:lot.properties"})
@ConfigurationProperties(prefix = "mqtt")
@Component
@Data
public class MqttConfig {
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String clientId;
}
