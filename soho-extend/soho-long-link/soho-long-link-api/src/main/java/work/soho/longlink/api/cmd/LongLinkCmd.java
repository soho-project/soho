package work.soho.longlink.api.cmd;

import lombok.Data;

@Data
public class LongLinkCmd {
    /**
     * 连接ID
     */
    private String connectId;

    /**
     * 用户ID
     */
    private String uid;

    /**
     * 消息体
     */
    private String payload;
}
