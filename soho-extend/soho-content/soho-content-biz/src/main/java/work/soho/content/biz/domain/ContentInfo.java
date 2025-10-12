package work.soho.content.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统内容表
 * @TableName admin_content
 */
@TableName(value ="content_info")
@Data
public class ContentInfo implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @ExcelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文章标题
     */
    @ApiModelProperty("文章标题")
    @ExcelProperty("文章标题")
    @TableField(value = "title")
    private String title;

    /**
     * 文章描述
     */
    @ApiModelProperty("文章描述")
    @ExcelProperty("文章描述")
    @TableField(value = "description")
    private String description;

    /**
     * 关键字
     */
    @ApiModelProperty("关键字")
    @ExcelProperty("关键字")
    @TableField(value = "keywords")
    private String keywords;

    /**
     * 缩略图
     */
    @ApiModelProperty("缩略图")
    @ExcelProperty("缩略图")
    @TableField(value = "thumbnail")
    private String thumbnail;

    /**
     * 文章内容
     */
    @ApiModelProperty("文章内容")
    @ExcelProperty("文章内容")
    @TableField(value = "body")
    private String body;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @ExcelProperty("创建时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 文章分类ID
     */
    @ApiModelProperty("文章分类ID")
    @ExcelProperty("文章分类")
    @TableField(value = "category_id")
    private Long categoryId;

    /**
     * 添加的管理员ID
     */
    @ApiModelProperty("添加的管理员ID")
    @ExcelProperty("管理员ID")
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 文章状态；  0 禁用  1 发布
     */
    @ApiModelProperty("文章状态；  0 禁用  1 发布")
    @ExcelProperty("文章状态")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
     * 是否置顶
     */
    @ApiModelProperty("是否置顶")
    @ExcelProperty("是否置顶")
    @TableField(value = "is_top")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer isTop;

    /**
     * 加星数量
     */
    @ApiModelProperty("加星数量")
    @ExcelProperty("加星数量")
    @TableField(value = "star")
    private Integer star;

    /**
     * like数量
     */
    @ApiModelProperty("like数量")
    @ExcelProperty("like数量")
    @TableField(value = "likes")
    private Integer likes;

    /**
     * 踩数据
     */
    @ApiModelProperty("踩数据")
    @ExcelProperty("踩数据")
    @TableField(value = "dis_likes")
    private Integer disLikes;

    /**
     * 评论数量
     */
    @ApiModelProperty("评论数量")
    @ExcelProperty("评论数量")
    @TableField(value = "comments_count")
    private Integer commentsCount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
