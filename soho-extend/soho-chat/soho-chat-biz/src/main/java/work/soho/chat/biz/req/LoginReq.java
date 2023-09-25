package work.soho.chat.biz.req;

import lombok.Data;

@Data
public class LoginReq {
    /**
     * 登录账号
     */
    private String id;

    /**
     * 登录用户密码
     */
    private String password;
}
