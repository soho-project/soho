package work.soho.content.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 内容分类
 * @TableName admin_content_category
 */
@TableName(value ="content_category")
@Data
public class ContentCategory implements Serializable {
    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelProperty(value = "id")
    private Long id;

    /**
     * 分类名称
     */
    @TableField(value = "name")
    @ExcelProperty(value = "名称")
    private String name;

    /**
     * 父分类ID
     */
    @TableField(value = "parent_id")
    @ExcelProperty(value = "父分类ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    /**
     * 分类描述
     */
    @TableField(value = "description")
    @ExcelProperty(value = "描述")
    private String description;

    /**
     * 分类关键字
     */
    @TableField(value = "keyword")
    @ExcelProperty(value = "关键字")
    private String keyword;

    /**
     * 分类内容
     */
    @TableField(value = "content")
    @ExcelProperty(value = "内容")
    private String content;

    /**
     * 排序
     */
    @TableField(value = "`order`")
    @ExcelProperty(value = "排序")
    private Integer order;

    /**
     * 分类是否显示 0 不显示  1 显示
     */
    @TableField(value = "is_display")
    @ExcelProperty(value = "是否显示")
    private Integer isDisplay;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ExcelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
