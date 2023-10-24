package work.soho.chat.biz.req;

import lombok.Data;

@Data
public class UpdatePasswordReq {
    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String password;
}
