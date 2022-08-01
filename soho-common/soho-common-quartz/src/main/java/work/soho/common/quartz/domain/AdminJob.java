package work.soho.common.quartz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName admin_job
 */
@TableName(value ="admin_job")
@Data
public class AdminJob implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 任务名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 能否并发执行
     */
    @TableField(value = "can_concurrency")
    private Integer canConcurrency;

    /**
     * 计划任务执行指令
     */
    @TableField(value = "cmd")
    private String cmd;

    /**
     * 状态 1 可执行  0 禁止执行
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * cron表达式
     */
    @TableField(value = "cron")
    private String cron;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private LocalDateTime createdTime;

    /**
     * 最后更新时间
     */
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}