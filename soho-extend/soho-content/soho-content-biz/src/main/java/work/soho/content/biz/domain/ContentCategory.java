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
@TableName(value ="content_category")
@ApiModel("内容分类")
public class ContentCategory implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * name
    */
    @ExcelProperty("name")
    @ApiModelProperty(value = "name")
    @TableField(value = "name")
    private String name;

    /**
    * parent_id
    */
    @ExcelProperty("parent_id")
    @ApiModelProperty(value = "parent_id")
    @TableField(value = "parent_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    /**
    * description
    */
    @ExcelProperty("description")
    @ApiModelProperty(value = "description")
    @TableField(value = "description")
    private String description;

    /**
    * keyword
    */
    @ExcelProperty("keyword")
    @ApiModelProperty(value = "keyword")
    @TableField(value = "keyword")
    private String keyword;

    /**
    * content
    */
    @ExcelProperty("content")
    @ApiModelProperty(value = "content")
    @TableField(value = "content")
    private String content;

    /**
    * order
    */
    @ExcelProperty("order")
    @ApiModelProperty(value = "order")
    @TableField(value = "`order`")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer order;

    /**
    * is_display
    */
    @ExcelProperty("is_display")
    @ApiModelProperty(value = "is_display")
    @TableField(value = "is_display")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer isDisplay;

    /**
    * update_time
    */
    @ExcelProperty("update_time")
    @ApiModelProperty(value = "update_time")
    @TableField(value = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
    * created_time
    */
    @ExcelProperty("created_time")
    @ApiModelProperty(value = "created_time")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}