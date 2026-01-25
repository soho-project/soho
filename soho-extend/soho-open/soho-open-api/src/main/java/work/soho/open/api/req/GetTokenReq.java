package work.soho.open.api.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 获取用户token
 */
@Data
public class GetTokenReq {
    @ApiModelProperty(value = "授权码")
    private String code;

    @ApiModelProperty(value = "客户端类型")
    private String clientCode;
}
