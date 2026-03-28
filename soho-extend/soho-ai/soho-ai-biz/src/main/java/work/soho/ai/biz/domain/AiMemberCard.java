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
@TableName("ai_member_card")
public class AiMemberCard implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @TableField("name")
    private String name;

    /**
     * monthly / quarterly / yearly
     */
    @TableField("card_type")
    private String cardType;

    /**
     * by_request / by_token
     */
    @TableField("limit_mode")
    private String limitMode;

    @TableField("validity_days")
    private Integer validityDays;

    @TableField("rate_limit_5h")
    private Integer rateLimit5h;

    @TableField("rate_limit_7d")
    private Integer rateLimit7d;

    @TableField("rate_limit_5h_enabled")
    private Boolean rateLimit5hEnabled;

    @TableField("rate_limit_7d_enabled")
    private Boolean rateLimit7dEnabled;

    @TableField("rate_limit_window_5h")
    private Integer rateLimitWindow5h;

    @TableField("rate_limit_window_7d")
    private Integer rateLimitWindow7d;

    @TableField("weekly_prompt_token_limit")
    private Integer weeklyPromptTokenLimit;

    @TableField("weekly_completion_token_limit")
    private Integer weeklyCompletionTokenLimit;

    @TableField("weekly_total_token_limit")
    private Integer weeklyTotalTokenLimit;

    @TableField("status")
    private Integer status;

    @TableField("sort")
    private Integer sort;

    @TableField("remark")
    private String remark;

    @TableField("updated_time")
    private LocalDateTime updatedTime;

    @TableField("created_time")
    private LocalDateTime createdTime;
}
