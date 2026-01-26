package work.soho.content.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 内容评论实体
 */
@TableName(value = "content_comment")
@Data
public class ContentComment implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 外部评论ID
     */
    @TableField(value = "external_comment_id")
    private Long externalCommentId;

    /**
     * 内容ID
     */
    @TableField(value = "content_id")
    private Long contentId;

    /**
     * 父评论ID
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 作者名称
     */
    @TableField(value = "author_name")
    private String authorName;

    /**
     * 作者邮箱
     */
    @TableField(value = "author_email")
    private String authorEmail;

    /**
     * 作者链接
     */
    @TableField(value = "author_url")
    private String authorUrl;

    /**
     * 评论内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 状态
     */
    @TableField(value = "status")
    private String status;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
