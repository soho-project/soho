package work.soho.ai.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("ai_user_member_card")
public class AiUserMemberCard implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @TableField("user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    @TableField("member_card_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long memberCardId;

    /**
     * 0:inactive 1:active 2:expired
     */
    @TableField("status")
    private Integer status;

    @TableField("priority")
    private Integer priority;

    @TableField("is_selected")
    private Boolean isSelected;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("activated_time")
    private LocalDateTime activatedTime;

    @TableField("source")
    private String source;

    @TableField("biz_no")
    private String bizNo;

    @TableField("updated_time")
    private LocalDateTime updatedTime;

    @TableField("created_time")
    private LocalDateTime createdTime;
}
