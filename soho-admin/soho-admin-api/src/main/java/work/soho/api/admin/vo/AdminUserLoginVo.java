package work.soho.api.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户登录
 */
@Data
public class AdminUserLoginVo {
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("验证码")
    private String captcha;
}
