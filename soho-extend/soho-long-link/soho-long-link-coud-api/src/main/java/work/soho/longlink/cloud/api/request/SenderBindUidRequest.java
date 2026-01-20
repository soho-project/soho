package work.soho.longlink.cloud.api.request;

import lombok.Data;

@Data
public class SenderBindUidRequest {
    private String uid;
    private String connectId;
}
