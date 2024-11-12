package work.soho.common.data.upload.adapter.s3;

import lombok.Data;

@Data
public class S3Properties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String urlPrefix;
}
