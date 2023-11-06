package work.soho.open.api.req;

import lombok.Data;

/**
 * 获取指定用户的code
 */
@Data
public class GetCodeReq extends BaseAuthReq<GetCodeReq.Body> {
    @Data
    public static class Body {
        private Long appId;
        private Long uid;
        private String sign;
    }
}
