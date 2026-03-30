package work.soho.user.biz.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserSimpleRegisterVo {
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;
}
