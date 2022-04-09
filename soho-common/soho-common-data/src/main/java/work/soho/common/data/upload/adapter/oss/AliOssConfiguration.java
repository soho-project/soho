package work.soho.common.data.upload.adapter.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "upload.oss", name = "enable", havingValue = "true")
@EnableConfigurationProperties(AliOssProperties.class)
public class AliOssConfiguration {
    /**
     * build oss client
     *
     * @param aliOssProperties
     * @return
     */
    @Bean
    public OSS build(AliOssProperties aliOssProperties) {
        return new OSSClientBuilder().build(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret());
    }
}
