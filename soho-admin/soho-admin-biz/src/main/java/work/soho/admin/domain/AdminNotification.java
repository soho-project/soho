package work.soho.admin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 管理员通知
 * @TableName admin_notification
 */
@TableName(value ="admin_notification")
@Data
public class AdminNotification implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 接收人
     */
    @TableField(value = "admin_user_id")
    private Integer adminUserId;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 创建者 0 为系统发送
     */
    @TableField(value = "create_admin_user_id")
    private Integer createAdminUserId;

    /**
     * 通知内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 创建时间
     */
    @TableField(value = "cteated_time")
    private LocalDateTime cteatedTime;

    /**
     * 是否已读 0 未读 1 已读
     */
    @TableField(value = "is_read")
    private Integer isRead;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}