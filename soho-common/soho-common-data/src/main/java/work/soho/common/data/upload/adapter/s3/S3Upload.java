package work.soho.common.data.upload.adapter.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import work.soho.common.data.upload.Upload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

@Log4j2
@RequiredArgsConstructor
@Service
@ConditionalOnBean(S3Properties.class)
public class S3Upload implements Upload {
    private S3Properties s3Properties;
    public S3Upload(S3Properties s3Properties) {
        this.s3Properties = s3Properties;
    }

    /**
     * 获取S3客户端
     *
     * @return
     */
    public S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(s3Properties.getAccessKey(), s3Properties.getSecretKey())))
                .endpointOverride(URI.create(s3Properties.getEndpoint()))
                .region(Region.US_EAST_1) // MinIO 默认区域
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    @Override
    public String uploadFile(String filePath, String content) {
        // 将字符串内容转换为 InputStream
        try (InputStream inputStream = new ByteArrayInputStream(content.getBytes())) {
            return uploadFile(filePath, inputStream);
        } catch (Exception e) {
            log.error("Upload failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String uploadFile(String filePath, InputStream inputStream) {
        try {
            // 使用 S3 PutObjectRequest 上传文件
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(filePath)
                    .build();

            // 执行上传操作
            PutObjectResponse putObjectResponse = s3Client().putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));
            System.out.println(putObjectResponse);
            return s3Properties.getUrlPrefix() + filePath; // 返回文件的 ETag 或者其他成功信息
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Upload failed: " + e.getMessage());
        }
        return null;
    }
}
