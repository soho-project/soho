package work.soho.open.api.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseAuthReq<T> {
    @ApiModelProperty(value = "签名")
    private String sign;

    @ApiModelProperty(value = "appId")
    private Long appId;

    @ApiModelProperty(value = "body")
    private T body;
}
