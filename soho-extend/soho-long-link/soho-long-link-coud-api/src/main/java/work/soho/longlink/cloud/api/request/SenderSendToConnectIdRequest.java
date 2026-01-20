package work.soho.longlink.cloud.api.request;

import lombok.Data;

@Data
public class SenderSendToConnectIdRequest {
    private String connectId;
    private String msg;
}
