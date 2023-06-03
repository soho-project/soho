package work.soho.content.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 系统内容表
 * @TableName admin_content
 */
@TableName(value ="admin_content")
@Data
public class AdminContent implements Serializable {
    /**
     *
     */
    @TableField(value = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 文章描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 关键字
     */
    @TableField(value = "keywords")
    private String keywords;

    /**
     * 缩略图
     */
    @TableField(value = "thumbnail")
    private String thumbnail;

    /**
     * 文章内容
     */
    @TableField(value = "body")
    private String body;

    /**
     * 创建时间
     */
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private LocalDateTime createdTime;

    /**
     * 文章分类ID
     */
    @TableField(value = "category_id")
    private Long categoryId;

    /**
     * 添加的管理员ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 文章状态；  0 禁用  1 发布
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 是否置顶
     */
    @TableField(value = "is_top")
    private Integer isTop;

    /**
     * 加星数量
     */
    @TableField(value = "star")
    private Integer star;

    /**
     * like数量
     */
    @TableField(value = "likes")
    private Integer likes;

    /**
     * 踩数据
     */
    @TableField(value = "dis_likes")
    private Integer disLikes;

    /**
     * 评论数量
     */
    @TableField(value = "comments_count")
    private Integer commentsCount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
