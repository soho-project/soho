package work.soho.chat.biz.req;

import lombok.Data;

/**
 * 创建禁言
 */
@Data
public class CreateGroupBannedReq {
    private Long groupId;
    private Long uid;

    /**
     * 禁言时长；单位秒
     */
    private Integer second;
}
