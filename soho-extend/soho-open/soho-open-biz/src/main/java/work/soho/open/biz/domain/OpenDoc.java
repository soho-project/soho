package work.soho.open.biz.domain;

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
@TableName(value ="open_doc")
public class OpenDoc implements Serializable {
    /**
    * 唯一标识
    */
    @ExcelProperty("唯一标识")
    @ApiModelProperty(value = "唯一标识")
    @TableField(value = "category_key")
    private String categoryKey;

    /**
    * 名称
    */
    @ExcelProperty("名称")
    @ApiModelProperty(value = "名称")
    @TableField(value = "title")
    private String title;

    /**
    * 文档内容
    */
    @ExcelProperty("文档内容")
    @ApiModelProperty(value = "文档内容")
    @TableField(value = "content")
    private String content;

    /**
    * 内容类型
    */
    @ExcelProperty("内容类型")
    @ApiModelProperty(value = "内容类型")
    @TableField(value = "content_format")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer contentFormat;

    /**
    * sort_no
    */
    @ExcelProperty("sort_no")
    @ApiModelProperty(value = "sort_no")
    @TableField(value = "sort_no")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer sortNo;

    /**
    * 状态
    */
    @ExcelProperty("状态")
    @ApiModelProperty(value = "状态")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * created_time
    */
    @ExcelProperty("created_time")
    @ApiModelProperty(value = "created_time")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * updated_time
    */
    @ExcelProperty("updated_time")
    @ApiModelProperty(value = "updated_time")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * 父级分类
    */
    @ExcelProperty("父级分类")
    @ApiModelProperty(value = "父级分类")
    @TableField(value = "parent_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    /**
    * 分类类型
    */
    @ExcelProperty("分类类型")
    @ApiModelProperty(value = "分类类型")
    @TableField(value = "category_type")
    private String categoryType;

}