package work.soho.user.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserLoginVo {
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("验证码")
    private String captcha;

    /**
     * 在手机发送短信的时候用
     */
    @ApiModelProperty("验证码Id")
    private String captcheId;
}
