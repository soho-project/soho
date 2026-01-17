package work.soho.longlink.api.event;


import lombok.Data;
import work.soho.longlink.api.message.LongLinkMessage;

@Data
public class MessageEvent {

    /**
     * 连接ID
     */
    private String connectId;

    /**
     * 用户ID
     */
    private String uid;

    /**
     * 原始消息报
     */
    private String payload;

    /**
     * 结构化消息
     */
    private LongLinkMessage message;
}
