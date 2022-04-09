package work.soho.common.data.upload.adapter.cos;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
public class CosProperties {
    private String secretId;
    private String secretKey;
    private String region;
    private String bucketName;
    private String urlPrefix;
}
