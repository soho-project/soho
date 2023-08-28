package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="chat_session_message_user")
@Data
public class ChatSessionMessageUser implements Serializable {
    /**
    * null
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 消息ID
    */
    @TableField(value = "message_id")
    private Long messageId;

    /**
    * 用户ID
    */
    @TableField(value = "uid")
    private Long uid;

    /**
    * 是否已读
    */
    @TableField(value = "is_read")
    private Integer isRead;

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
     * 会话ID
     */
    @TableField(value = "session_id")
    private Long sessionId;

}
