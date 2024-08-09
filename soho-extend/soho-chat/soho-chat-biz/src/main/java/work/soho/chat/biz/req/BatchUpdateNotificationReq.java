package work.soho.chat.biz.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BatchUpdateNotificationReq {
    /**
     * 会话ID列表
     */
    @ApiModelProperty(value = "会话ID列表")
    private List<Long> sessionIds = new ArrayList<>();

    /**
     * 消息提示类型
     * 1 接收并提醒
     * 2 接收不提醒
     * 3 屏蔽消息
     */
    @ApiModelProperty(value = "消息提示类型")
    private Integer type;
}
