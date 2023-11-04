package work.soho.chat.biz.vo;

import lombok.Data;

@Data
public class SessionUserVO {
    /**
     * ID
     */
    private Long id;

    /**
     * 会话ID
     */
    private Long sessionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 会话显示昵称
     */
    private String sessionNickname;

    /**
     * 能否发送消息
     */
    private Integer canSend;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 好友备注名
     */
    private String notesName;
}
