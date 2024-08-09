package work.soho.chat.biz.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 邀请用户入群请求
 */
@Data
public class InviteJoinGroupReq {
    @ApiModelProperty(value = "会话ID")
    private Long sessionId;

    @ApiModelProperty(value = "用户ID列表")
    private List<Long> chatUserIds;
}
