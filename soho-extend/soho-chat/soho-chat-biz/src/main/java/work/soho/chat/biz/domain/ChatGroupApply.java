package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="chat_group_apply")
@Data
public class ChatGroupApply implements Serializable {
    /**
    * null
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 申请用户ID
    */
    @TableField(value = "chat_uid")
    private Long chatUid;

    /**
    * 群聊ID
    */
    @TableField(value = "group_id")
    private Long groupId;

    /**
    * 申请状态;0:待处理,1:同意,2:拒绝
    */
    @TableField(value = "status")
    private Integer status;

    /**
    * 问题
    */
    @TableField(value = "ask")
    private String ask;

    /**
    * 回答
    */
    @TableField(value = "answer")
    private String answer;

    /**
    * null
    */
    @TableField(value = "apply_message")
    private String applyMessage;

    /**
    * 更新时间
    */
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 审批消息
    */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 申请单处理ID
     */
    @TableField(value = "apply_do_uid")
    private Long applyDoUid;
}
