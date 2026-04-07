package work.soho.admin.biz.domain;

import java.io.Serializable;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 管理员通知
 *
 * @TableName admin_notification
 */
@Data
@TableName("admin_notification")
public class AdminNotification implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 创建者 0 为系统发送
     */
    @ApiModelProperty("创建者 0 为系统发送")
    private Long createAdminUserId;

    /**
     * 发送者类型：admin/user/system
     */
    @ApiModelProperty("发送者类型：admin/user/system")
    @TableField("sender_type")
    private String senderType;

    /**
     * 发送者ID
     */
    @ApiModelProperty("发送者ID")
    @TableField("sender_id")
    private Long senderId;

    /**
     * 通知内容
     */
    @ApiModelProperty("通知内容")
    private String content;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 接收者类型，仅用于管理端筛选与展示，不落库。
     */
    @ApiModelProperty("接收者类型：admin/user")
    @TableField(exist = false)
    private String receiverType;

    /**
     * 接收人，仅用于兼容旧页面查询，不落库。
     */
    @ApiModelProperty("接收人")
    @TableField(exist = false)
    private Long adminUserId;

    /**
     * 接收者 ID，仅用于管理端筛选，不落库。
     */
    @ApiModelProperty("接收者ID")
    @TableField(exist = false)
    private Long receiverId;

    /**
     * 是否已读，仅用于兼容旧查询，不落库。
     */
    @ApiModelProperty("是否已读 0 未读 1 已读")
    @TableField(exist = false)
    private Integer isRead;

    /**
     * 接收人数。
     */
    @TableField(exist = false)
    private Integer receiverCount;

    /**
     * 已读人数。
     */
    @TableField(exist = false)
    private Integer readCount;

    /**
     * 未读人数。
     */
    @TableField(exist = false)
    private Integer unreadCount;

}
