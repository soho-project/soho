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

@TableName(value ="chat_user_friend_apply")
@Data
public class ChatUserFriendApply implements Serializable {
    /**
    * ID
    */
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 聊天用户ID
    */
    @ApiModelProperty(value = "聊天用户ID")
    @TableField(value = "chat_uid")
    private Long chatUid;

    /**
    * 好友用户ID
    */
    @ApiModelProperty(value = "好友用户ID")
    @TableField(value = "friend_uid")
    private Long friendUid;

    /**
    * 申请状态;0:待处理,1:已同意,2:已拒绝
    */
    @ApiModelProperty(value = "申请状态;0:待处理,1:已同意,2:已拒绝")
    @TableField(value = "status")
    private Integer status;

    /**
    * 提问的问题
    */
    @ApiModelProperty(value = "提问的问题")
    @TableField(value = "ask")
    private String ask;

    /**
    * 回答答案
    */
    @ApiModelProperty(value = "回答答案")
    @TableField(value = "answer")
    private String answer;

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
}
