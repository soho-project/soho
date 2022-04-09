package work.soho.common.data.upload.adapter.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import lombok.RequiredArgsConstructor;
import work.soho.common.data.upload.Upload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

@RequiredArgsConstructor
public class CosUpload implements Upload {
    private final CosProperties cosProperties;

    public COSClient getCosClient() {
        COSCredentials cred = new BasicCOSCredentials(cosProperties.getSecretId(), cosProperties.getSecretKey());
        Region region = new Region(cosProperties.getRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(cred, clientConfig);
    }

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
        COSClient cosClient = getCosClient();
        cosClient.putObject(putObjectRequest);
        cosClient.shutdown();
        return cosProperties.getUrlPrefix() + filePath;
    }
}
