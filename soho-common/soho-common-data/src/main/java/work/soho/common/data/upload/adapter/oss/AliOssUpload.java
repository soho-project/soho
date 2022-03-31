package work.soho.common.data.upload.adapter.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import work.soho.common.data.upload.Upload;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class AliOssUpload implements Upload {
    private final OSS oss;
    private final AliOssProperties aliOssProperties;
    private final Logger logger = LoggerFactory.getLogger(AliOssUpload.class);

    @Override
    public String uploadFile(String filePath, String content) {
        return uploadFile(filePath, new ByteArrayInputStream(content.getBytes()));
    }

    @Override
    public String uploadFile(String filePath, InputStream inputStream) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(aliOssProperties.getBucketName(), filePath
                    , inputStream);
            oss.putObject(putObjectRequest);
            return aliOssProperties.getUrlPrefix() + filePath;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }
}
