package work.soho.user.api.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String code;
}
