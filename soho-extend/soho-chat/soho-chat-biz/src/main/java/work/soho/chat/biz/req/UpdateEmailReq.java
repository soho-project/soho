package work.soho.chat.biz.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateEmailReq {
    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "验证码")
    private Integer code;
}
