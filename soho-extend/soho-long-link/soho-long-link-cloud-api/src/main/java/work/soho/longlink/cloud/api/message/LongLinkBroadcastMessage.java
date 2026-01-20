package work.soho.longlink.cloud.api.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LongLinkBroadcastMessage {
    private Action action;
    private String uid;
    private String connectId;
    private String msg;

    public enum Action {
        SEND_TO_UID,
        SEND_TO_CONNECT_ID,
        SEND_TO_ALL_UID,
        SEND_TO_ALL_CONNECT,
        BIND_UID
    }
}
