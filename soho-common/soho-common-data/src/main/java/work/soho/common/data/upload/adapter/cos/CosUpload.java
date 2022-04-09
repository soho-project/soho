package work.soho.common.data.upload.adapter.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import work.soho.common.data.upload.Upload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

@RequiredArgsConstructor
@Service
@ConditionalOnBean(CosProperties.class)
public class CosUpload implements Upload {
    private final COSClient cosClient;
    private final CosProperties cosProperties;

    @Override
    public String uploadFile(String filePath, String content) {
        return uploadFile(filePath, new ByteArrayInputStream(content.getBytes()));
    }

    @Override
    public String uploadFile(String filePath, InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setHeader("expires", new Date(1660000000000L));
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosProperties.getBucketName(),
                filePath, inputStream, objectMetadata);
        cosClient.putObject(putObjectRequest);
        cosClient.shutdown();
        return cosProperties.getUrlPrefix() + filePath;
    }
}
