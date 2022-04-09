package work.soho.common.data.upload.adapter.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
public class AliOssProperties {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String urlPrefix;
}
