package work.soho.longlink.cloud.api.request;

import lombok.Data;

@Data
public class SenderSendToUidRequest {
    private String uid;
    private String msg;
}
