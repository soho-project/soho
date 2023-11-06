package work.soho.open.api.req;

import lombok.Data;

@Data
public class BaseAuthReq<T> {
    private String sign;
    private Long appId;
    private T body;
}
