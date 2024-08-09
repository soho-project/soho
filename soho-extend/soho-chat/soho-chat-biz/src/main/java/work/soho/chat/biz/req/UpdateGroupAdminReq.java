package work.soho.chat.biz.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateGroupAdminReq {
    @ApiModelProperty(value = "用户ID")
    private Long uid;

    @ApiModelProperty(value = "群组ID")
    private Long groupId;

    @ApiModelProperty(value = "是否是管理员;0:不是,1:是")
    private Integer isAdmin;
}
