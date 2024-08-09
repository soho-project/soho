package work.soho.content.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统内容表
 * @TableName admin_content
 */
@TableName(value ="admin_content")
@Data
public class AdminContent implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文章标题
     */
    @ApiModelProperty("文章标题")
    @TableField(value = "title")
    private String title;

    /**
     * 文章描述
     */
    @ApiModelProperty("文章描述")
    @TableField(value = "description")
    private String description;

    /**
     * 关键字
     */
    @ApiModelProperty("关键字")
    @TableField(value = "keywords")
    private String keywords;

    /**
     * 缩略图
     */
    @ApiModelProperty("缩略图")
    @TableField(value = "thumbnail")
    private String thumbnail;

    /**
     * 文章内容
     */
    @ApiModelProperty("文章内容")
    @TableField(value = "body")
    private String body;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField(value = "created_time")
    private LocalDateTime createdTime;

    /**
     * 文章分类ID
     */
    @ApiModelProperty("文章分类ID")
    @TableField(value = "category_id")
    private Long categoryId;

    /**
     * 添加的管理员ID
     */
    @ApiModelProperty("添加的管理员ID")
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 文章状态；  0 禁用  1 发布
     */
    @ApiModelProperty("文章状态；  0 禁用  1 发布")
    @TableField(value = "status")
    private Integer status;

    /**
     * 是否置顶
     */
    @ApiModelProperty("是否置顶")
    @TableField(value = "is_top")
    private Integer isTop;

    /**
     * 加星数量
     */
    @ApiModelProperty("加星数量")
    @TableField(value = "star")
    private Integer star;

    /**
     * like数量
     */
    @ApiModelProperty("like数量")
    @TableField(value = "likes")
    private Integer likes;

    /**
     * 踩数据
     */
    @ApiModelProperty("踩数据")
    @TableField(value = "dis_likes")
    private Integer disLikes;

    /**
     * 评论数量
     */
    @ApiModelProperty("评论数量")
    @TableField(value = "comments_count")
    private Integer commentsCount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
