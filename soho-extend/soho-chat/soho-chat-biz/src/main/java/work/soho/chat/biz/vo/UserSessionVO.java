package work.soho.chat.biz.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户消息会话信息
 */
@Data
public class UserSessionVO {
    /**
     * 会话ID
     */
    private Long id;

    /**
     * 会话类型;1:私聊,2:群聊,3:群组;frontType:select
     */
    private Integer type;

    /**
     * 状态;1:活跃,2:禁用,3:删除;frontType:select
     */
    private Integer status;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 别名标题
     */
    private String aliasTitle;

    /**
     * 会话头像
     */
    private String avatar;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 会话消息最后时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageTime;

    /**
     * 用户回话最后查看时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLookMessageTime;
}
