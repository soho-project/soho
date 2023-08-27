package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="chat_session_user")
@Data
public class ChatSessionUser implements Serializable {
    /**
    * null
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 会话ID
    */
    @TableField(value = "session_id")
    private Long sessionId;

    /**
    * 用户ID
    */
    @TableField(value = "user_id")
    private Long userId;

    /**
    * 更新时间
    */
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 会话显示头像
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 会话显示标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 会话显示昵称
     */
    @TableField(value = "session_nickname")
    private String sessionNickname;

    /**
     * 会话用户查看会话最后时间
     */
    @TableField(value = "last_look_message_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLookMessageTime;

    /**
     * 消息置顶
     */
    @TableField(value = "is_top")
    private Integer isTop;

    /**
     * 免打扰
     */
    @TableField(value = "is_not_disturb")
    private Integer isNotDisturb;

    /**
     * 屏蔽会话
     */
    @TableField(value = "is_shield")
    private Integer isShield;
}
