package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="chat_session_user")
@Data
public class ChatSessionUser implements Serializable {
    /**
    * ID
    */
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 会话ID
    */
    @ApiModelProperty(value = "会话ID")
    @TableField(value = "session_id")
    private Long sessionId;

    /**
    * 用户ID
    */
    @ApiModelProperty(value = "用户ID")
    @TableField(value = "user_id")
    private Long userId;

    /**
    * 更新时间
    */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 会话显示头像
     */
    @ApiModelProperty(value = "会话显示头像")
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 会话显示标题
     */
    @ApiModelProperty(value = "会话显示标题")
    @TableField(value = "title")
    private String title;

    /**
     * 原始标题
     */
    @ApiModelProperty(value = "原始标题")
    @TableField(value = "origin_title")
    private String originTitle;

    /**
     * 会话显示昵称
     */
    @ApiModelProperty(value = "会话显示昵称")
    @TableField(value = "session_nickname")
    private String sessionNickname;

    /**
     * 会话用户查看会话最后时间
     */
    @ApiModelProperty(value = "会话用户查看会话最后时间")
    @TableField(value = "last_look_message_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLookMessageTime;

    /**
     * 消息置顶
     */
    @ApiModelProperty(value = "消息置顶")
    @TableField(value = "is_top")
    private Integer isTop;

    /**
     * 免打扰
     */
    @ApiModelProperty(value = "免打扰")
    @TableField(value = "is_not_disturb")
    private Integer isNotDisturb;

    /**
     * 屏蔽会话
     */
    @ApiModelProperty(value = "屏蔽会话")
    @TableField(value = "is_shield")
    private Integer isShield;

    /**
     * 会话用户状态
     */
    @ApiModelProperty(value = "会话用户状态")
    @TableField(value = "status")
    private Integer status;

    /**
     * 未读消息总数
     */
    @ApiModelProperty(value = "未读消息总数")
    @TableField(value = "unread_count")
    private Integer unreadCount;

    /**
     * 能否发送消息
     */
    @ApiModelProperty(value = "能否发送消息")
    @TableField(value = "can_send")
    private Integer canSend;
}
