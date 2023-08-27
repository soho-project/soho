package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="chat_user_notice")
@Data
public class ChatUserNotice implements Serializable {
    /**
    * ID
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 聊天用户ID
    */
    @TableField(value = "chat_uid")
    private Long chatUid;

    /**
    * 通知处理状态;0:待处理,1:同意,2:拒绝,3:已处理
    */
    @TableField(value = "status")
    private Integer status;

    /**
    * 业务类型;1:好友申请
    */
    @TableField(value = "type")
    private Integer type;

    /**
    * 跟踪ID
    */
    @TableField(value = "tracking_id")
    private Long trackingId;

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
