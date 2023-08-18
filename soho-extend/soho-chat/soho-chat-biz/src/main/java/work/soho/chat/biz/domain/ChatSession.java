package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@TableName(value ="chat_session")
@Data
public class ChatSession implements Serializable {
    /**
    * null
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 会话类型;1:私聊,2:群聊,3:群组;frontType:select
    */
    @TableField(value = "type")
    private Integer type;

    /**
    * 状态;1:活跃,2:禁用,3:删除;frontType:select
    */
    @TableField(value = "status")
    private Integer status;

    /**
    * 会话标题
    */
    @TableField(value = "title")
    private String title;

    /**
    * 会话头像
    */
    @TableField(value = "avatar")
    private String avatar;

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
     * 会话消息最后时间
     */
    @TableField(value = "last_message_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageTime;
}
