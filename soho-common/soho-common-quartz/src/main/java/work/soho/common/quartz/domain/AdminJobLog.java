package work.soho.common.quartz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 计划任务执行日志
 * @TableName admin_job_log
 */
@TableName(value ="admin_job_log")
@Data
public class AdminJobLog implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 任务ID
     */
    @TableField(value = "job_id")
    private Integer jobId;

    /**
     * 任务创建时间
     */
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    /**
     * 任务执行状态; 1 执行中  2 执行完成  3 任务取消
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 任务执行返回结果
     */
    @TableField(value = "result")
    private String result;

    /**
     * 任务结束时间
     */
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    /**
     * 手动执行时候的用户ID
     */
    @TableField(value = "admin_id")
    private Integer adminId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
