package work.soho.longlink.api.event;


import lombok.Data;

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
}
