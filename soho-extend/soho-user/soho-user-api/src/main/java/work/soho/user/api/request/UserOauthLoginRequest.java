package work.soho.user.api.request;

import lombok.Data;

@Data
public class UserOauthLoginRequest {
    private String code;
}
