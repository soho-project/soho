package work.soho.common.data.upload.adapter.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import work.soho.common.data.upload.Upload;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Primary
@RequiredArgsConstructor
@Service
@ConditionalOnBean(AliOssProperties.class)
public class AliOssUpload implements Upload {
    private final AliOssProperties aliOssProperties;
    private final Logger logger = LoggerFactory.getLogger(AliOssUpload.class);

    /**
     * 构建 OSS 客户端。
     *
     * @return OSS 客户端
     */
    public OSS getClient() {
        String endpoint = normalizeEndpoint();
        String accessKeyId = sanitize(aliOssProperties.getAccessKeyId());
        String accessKeySecret = sanitize(aliOssProperties.getAccessKeySecret());
        if (!StringUtils.hasText(endpoint) || !StringUtils.hasText(accessKeyId) || !StringUtils.hasText(accessKeySecret)) {
            throw new IllegalArgumentException("oss config invalid: endpoint/accessKeyId/accessKeySecret is blank");
        }
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 上传文本内容。
     *
     * @param filePath 文件路径
     * @param content 文本内容
     * @return 访问地址
     */
    @Override
    public String uploadFile(String filePath, String content) {
        return uploadFile(filePath, new ByteArrayInputStream(content.getBytes()));
    }

    /**
     * 上传输入流内容。
     *
     * @param filePath 文件路径
     * @param inputStream 文件输入流
     * @return 访问地址
     */
    @Override
    public String uploadFile(String filePath, InputStream inputStream) {
        String bucketName = sanitize(aliOssProperties.getBucketName());
        String endpoint = normalizeEndpoint();
        String urlPrefix = normalizeUrlPrefix();
        if (!StringUtils.hasText(bucketName)) {
            throw new IllegalArgumentException("oss config invalid: bucketName is blank");
        }
        OSS client = null;
        try {
            client = getClient();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filePath
                    , inputStream);
            client.putObject(putObjectRequest);
            return urlPrefix + filePath;
        } catch (Exception e) {
            logger.error("oss upload failed, endpoint={}, bucketName={}, filePath={}, msg={}",
                    endpoint, bucketName, filePath, e.getMessage(), e);
            return null;
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    /**
     * 标准化 endpoint，兼容包含协议或 bucket 子域名的配置。
     *
     * @return 标准化后的 endpoint
     */
    private String normalizeEndpoint() {
        String endpoint = sanitize(aliOssProperties.getEndpoint());
        if (!StringUtils.hasText(endpoint)) {
            return endpoint;
        }
        endpoint = endpoint.replaceFirst("^https?://", "");
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        String bucketName = sanitize(aliOssProperties.getBucketName());
        String prefix = StringUtils.hasText(bucketName) ? bucketName + "." : "";
        if (StringUtils.hasText(prefix)
                && endpoint.startsWith(prefix)
                && endpoint.endsWith(".aliyuncs.com")) {
            return endpoint.substring(prefix.length());
        }
        return endpoint;
    }

    /**
     * 标准化 URL 前缀，确保末尾包含 /。
     *
     * @return 标准化后的 URL 前缀
     */
    private String normalizeUrlPrefix() {
        String prefix = sanitize(aliOssProperties.getUrlPrefix());
        if (!StringUtils.hasText(prefix)) {
            throw new IllegalArgumentException("oss config invalid: urlPrefix is blank");
        }
        return prefix.endsWith("/") ? prefix : prefix + "/";
    }

    /**
     * 清洗配置值两端空白，避免签名串受不可见字符影响。
     *
     * @param value 原始值
     * @return 清洗后的值
     */
    private String sanitize(String value) {
        return value == null ? null : value.trim();
    }
}
