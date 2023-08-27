package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="chat_user_friend_apply")
@Data
public class ChatUserFriendApply implements Serializable {
    /**
    * null
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * null
    */
    @TableField(value = "chat_uid")
    private Long chatUid;

    /**
    * null
    */
    @TableField(value = "friend_uid")
    private Long friendUid;

    /**
    * 申请状态;0:待处理,1:已同意,2:已拒绝
    */
    @TableField(value = "status")
    private Integer status;

    /**
    * 提问的问题
    */
    @TableField(value = "ask")
    private String ask;

    /**
    * 回答答案
    */
    @TableField(value = "answer")
    private String answer;

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

}
