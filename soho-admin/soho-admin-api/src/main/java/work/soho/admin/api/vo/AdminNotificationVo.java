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
     * 接收人ID
     */
    @ApiModelProperty("接收人ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long adminUserId;

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
}
