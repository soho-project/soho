package work.soho.chat.biz.req;

import lombok.Data;

@Data
public class UpdateEmailReq {
    private String email;
    private Integer code;
}
