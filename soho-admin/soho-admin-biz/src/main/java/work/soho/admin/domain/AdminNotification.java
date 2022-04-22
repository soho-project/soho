package work.soho.admin.domain;

import java.io.Serializable;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 管理员通知
 *
 * @TableName admin_notification
 */
@Data
public class AdminNotification implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接收人
     */
    @ApiModelProperty("接收人")
    private Integer adminUserId;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 创建者 0 为系统发送
     */
    @ApiModelProperty("创建者 0 为系统发送")
    private Integer createAdminUserId;

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
    private LocalDateTime cteatedTime;

    /**
     * 是否已读 0 未读 1 已读
     */
    @ApiModelProperty("是否已读 0 未读 1 已读")
    private Integer isRead;

}
