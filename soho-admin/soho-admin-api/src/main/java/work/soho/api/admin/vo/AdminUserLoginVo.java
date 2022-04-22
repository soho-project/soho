package work.soho.api.admin.vo;

import lombok.Data;

/**
 * 用户登录
 */
@Data
public class AdminUserLoginVo {
    private String username;
    private String password;
    private String captcha;
}
