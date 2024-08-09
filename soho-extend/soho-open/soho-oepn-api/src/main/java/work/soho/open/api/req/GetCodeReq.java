package work.soho.open.api.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 获取指定用户的code
 */
@Data
public class GetCodeReq extends BaseAuthReq<GetCodeReq.Body> {
    @Data
    public static class Body {
        @ApiModelProperty(value = "appId")
        private Long appId;

        @ApiModelProperty(value = "用户ID")
        private Long uid;

        @ApiModelProperty(value = "签名")
        private String sign;
    }
}
