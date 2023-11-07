package work.soho.chat.biz.req;

import lombok.Data;

@Data
public class UpdatePhoneReq {
    private String phone;
    private Integer code;
}
