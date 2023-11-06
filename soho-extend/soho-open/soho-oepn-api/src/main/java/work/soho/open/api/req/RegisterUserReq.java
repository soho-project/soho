package work.soho.open.api.req;

import lombok.Data;

@Data
public class RegisterUserReq extends BaseAuthReq<RegisterUserReq.Body>{

    @Data
    public static class Body {
        private String username;
        private String nickname;
        private Integer age;
        private String email;
        private String phone;
        private String originId;
    }
}
