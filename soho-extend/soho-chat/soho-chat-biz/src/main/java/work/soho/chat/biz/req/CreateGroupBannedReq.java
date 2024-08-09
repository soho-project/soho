package work.soho.chat.biz.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创建禁言
 */
@Data
public class CreateGroupBannedReq {
    /**
     * 群组ID
     */
    @ApiModelProperty(required = true, value = "群组ID")
    private Long groupId;

    /**
     * 用户ID
     */
    @ApiModelProperty(required = true, value = "用户ID")
    private Long uid;

    /**
     * 禁言时长；单位秒
     */
    @ApiModelProperty(required = true, value = "禁言时长；单位秒")
    private Integer second;
}
