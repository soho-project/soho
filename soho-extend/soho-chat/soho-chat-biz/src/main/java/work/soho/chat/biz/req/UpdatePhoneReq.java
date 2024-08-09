package work.soho.chat.biz.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdatePhoneReq {
    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "验证码")
    private Integer code;
}
