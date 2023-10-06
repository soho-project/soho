package work.soho.chat.biz.req;

import lombok.Data;
import java.util.List;

@Data
public class SendMessageReq {
    /**
     * 消息类型
     *
     * 1 系统消息
     * 2 用户消息
     */
    private Integer msgType;

    /**
     * 会话列表
     */
    private List<Long> sessionIds;

    /**
     * 消息内容
     */
    private String content;
}
