package work.soho.admin.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 通知接收人及已读状态。
 */
@Data
@TableName("admin_notification_receiver")
public class AdminNotificationReceiver implements Serializable {
    /**
     * 主键 ID。
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 通知 ID。
     */
    @ApiModelProperty("通知ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long notificationId;

    /**
     * 接收者类型：admin/user。
     */
    @ApiModelProperty("接收者类型：admin/user")
    private String receiverType;

    /**
     * 接收者 ID。
     */
    @ApiModelProperty("接收者ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long receiverId;

    /**
     * 是否已读：0 未读，1 已读。
     */
    @ApiModelProperty("是否已读：0未读，1已读")
    private Integer isRead;

    /**
     * 已读时间。
     */
    @ApiModelProperty("已读时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readTime;
}
