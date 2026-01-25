package work.soho.open.api.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RegisterUserReq extends BaseAuthReq<RegisterUserReq.Body>{

    @Data
    public static class Body {
        @ApiModelProperty(value = "用户名")
        private String username;

        @ApiModelProperty(value = "昵称")
        private String nickname;

        @ApiModelProperty(value = "年龄")
        private Integer age;

        @ApiModelProperty(value = "邮箱")
        private String email;

        @ApiModelProperty(value = "手机号")
        private String phone;

        @ApiModelProperty(value = "原始用户ID")
        private String originId;
    }
}
