package work.soho.common.data.upload.adapter.qiniu;

import lombok.Data;

@Data
public class QiniuProperties {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String urlPrefix;
}
