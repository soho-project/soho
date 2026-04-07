package work.soho.admin.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AdminNotificationVo {
    /**
     * ID
     */
    @ApiModelProperty("ID")
    private Long id;

    /**
     * 通知主表 ID。
     */
    @ApiModelProperty("通知ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long notificationId;

    /**
     * 接收人ID
     */
    @ApiModelProperty("接收人ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long adminUserId;

    /**
     * 接收者类型
     */
    @ApiModelProperty("接收者类型：admin/user")
    private String receiverType;

    /**
     * 接收者ID
     */
    @ApiModelProperty("接收者ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long receiverId;

    /**
     * 接收人
     */
    @ApiModelProperty("接收人")
    private String adminUser;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 创建者 0 为系统发送
     */
    @ApiModelProperty("创建者 0 为系统发送")
    private String createAdminUser;

    /**
     * 发送者类型
     */
    @ApiModelProperty("发送者类型：admin/user/system")
    private String senderType;

    /**
     * 发送者ID
     */
    @ApiModelProperty("发送者ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
     * 是否已读 0 未读 1 已读
     */
    @ApiModelProperty("是否已读 0 未读 1 已读")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer isRead;

    /**
     * 接收人数。
     */
    @ApiModelProperty("接收人数")
    private Integer receiverCount;

    /**
     * 已读人数。
     */
    @ApiModelProperty("已读人数")
    private Integer readCount;

    /**
     * 未读人数。
     */
    @ApiModelProperty("未读人数")
    private Integer unreadCount;
}
