package work.soho.content.biz.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName(value ="content_info")
@ApiModel("系统内容表")
public class ContentInfo implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * 文章标题
    */
    @ExcelProperty("文章标题")
    @ApiModelProperty(value = "文章标题")
    @TableField(value = "title")
    private String title;

    /**
    * 文章描述
    */
    @ExcelProperty("文章描述")
    @ApiModelProperty(value = "文章描述")
    @TableField(value = "description")
    private String description;

    /**
    * 关键字
    */
    @ExcelProperty("关键字")
    @ApiModelProperty(value = "关键字")
    @TableField(value = "keywords")
    private String keywords;

    /**
    * 缩略图
    */
    @ExcelProperty("缩略图")
    @ApiModelProperty(value = "缩略图")
    @TableField(value = "thumbnail")
    private String thumbnail;

    /**
    * 文章内容
    */
    @ExcelProperty("文章内容")
    @ApiModelProperty(value = "文章内容")
    @TableField(value = "body")
    private String body;

    /**
    * 创建时间
    */
    @ExcelProperty("创建时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ExcelProperty("创建时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 文章分类ID
    */
    @ExcelProperty("文章分类ID")
    @ApiModelProperty(value = "文章分类ID")
    @TableField(value = "category_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long categoryId;

    /**
    * 添加的管理员ID
    */
    @ExcelProperty("添加的管理员ID")
    @ApiModelProperty(value = "添加的管理员ID")
    @TableField(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
    * 文章状态
    */
    @ExcelProperty("文章状态")
    @ApiModelProperty(value = "文章状态")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * 是否置顶
    */
    @ExcelProperty("是否置顶")
    @ApiModelProperty(value = "是否置顶")
    @TableField(value = "is_top")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer isTop;

    /**
    * star数量
    */
    @ExcelProperty("star数量")
    @ApiModelProperty(value = "star数量")
    @TableField(value = "star")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer star;

    /**
    * 点赞数量
    */
    @ExcelProperty("点赞数量")
    @ApiModelProperty(value = "点赞数量")
    @TableField(value = "likes")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer likes;

    /**
    * 踩踏数
    */
    @ExcelProperty("踩踏数")
    @ApiModelProperty(value = "踩踏数")
    @TableField(value = "dis_likes")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer disLikes;

    /**
    * 评论数量
    */
    @ExcelProperty("评论数量")
    @ApiModelProperty(value = "评论数量")
    @TableField(value = "comments_count")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer commentsCount;

}