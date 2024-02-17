package work.soho.mobile.verification.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SaveMsgRequest {
    private String phoneNumber;
    private String msg;
}
