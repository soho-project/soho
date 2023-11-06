package work.soho.open.api.req;

import lombok.Data;

/**
 * 获取用户token
 */
@Data
public class GetTokenReq {
    private String code;
    private String clientCode;
}
