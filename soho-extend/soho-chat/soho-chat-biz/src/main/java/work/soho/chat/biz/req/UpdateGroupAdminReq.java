package work.soho.chat.biz.req;

import lombok.Data;

@Data
public class UpdateGroupAdminReq {
    private Long uid;
    private Long groupId;
    private Integer isAdmin;
}
