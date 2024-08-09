package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="chat_session_message")
@Data
public class ChatSessionMessage implements Serializable {
    /**
    * ID
    */
    @ApiModelProperty(value = "ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 消息发送用户ID
    */
    @ApiModelProperty(value = "消息发送用户ID")
    @TableField(value = "from_uid")
    private Long fromUid;

    /**
    * 会话ID
    */
    @ApiModelProperty(value = "会话ID")
    @TableField(value = "session_id")
    private Long sessionId;

    /**
    * 消息内容
    */
    @ApiModelProperty(value = "消息内容")
    @TableField(value = "content")
    private String content;

    /**
    * 更新时间
    */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 客户端消息ID
     */
    @ApiModelProperty(value = "客户端消息ID")
    @TableField(value = "client_message_id")
    private String clientMessageId;

    /**
     * 是否已经删除
     */
    @ApiModelProperty(value = "是否已经删除")
    @TableField(value = "is_deleted")
    private Integer isDeleted;
}
