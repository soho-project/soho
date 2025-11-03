package work.soho.user.api.vo;

import lombok.Data;

@Data
public class UserRegisterVo {
    private String username;
    private String nickname;
    private String password;
    private String email;
    private String phone;
    private String verifyCode;
    private Long inviteCode;
    private String codeId;
}
