package work.soho.common.data.sms.channel.tencent;

import lombok.Data;

@Data
public class TencentProperties {
    private String secretId;
    private String secretKey;
    private String endpoint;
    private String region;
    private String sdkAppid;
}
