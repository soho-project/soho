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

    /**
     * 客户端字符串ID
     *
     * 客户端唯一识别ID
     */
    private String clientId;
}
