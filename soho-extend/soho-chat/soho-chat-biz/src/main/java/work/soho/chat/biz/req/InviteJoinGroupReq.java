package work.soho.chat.biz.req;

import lombok.Data;

import java.util.List;

/**
 * 邀请用户入群请求
 */
@Data
public class InviteJoinGroupReq {
    private Long sessionId;
    private List<Long> chatUserIds;
}
