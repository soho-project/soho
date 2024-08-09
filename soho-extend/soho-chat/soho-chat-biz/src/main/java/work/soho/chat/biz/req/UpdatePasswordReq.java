package work.soho.chat.biz.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdatePasswordReq {
    /**
     * 旧密码
     */
    @ApiModelProperty(value = "旧密码")
    private String oldPassword;

    /**
     * 新密码
     */
    @ApiModelProperty(value = "新密码")
    private String password;
}
