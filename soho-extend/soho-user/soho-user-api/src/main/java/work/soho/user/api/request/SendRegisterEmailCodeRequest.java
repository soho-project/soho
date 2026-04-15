package work.soho.user.api.request;

import lombok.Data;

@Data
public class SendRegisterEmailCodeRequest {
    private String email;
}

