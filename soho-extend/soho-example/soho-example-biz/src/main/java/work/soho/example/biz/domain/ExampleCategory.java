package work.soho.example.biz.domain;

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
@TableName(value ="example_category")
@ApiModel("分类样例")
public class ExampleCategory implements Serializable {
    /**
    * ID
    */
    @ExcelProperty("ID")
    @ApiModelProperty(value = "ID")
    @TableField(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer id;

    /**
    * 标题
    */
    @ExcelProperty("标题")
    @ApiModelProperty(value = "标题")
    @TableField(value = "title")
    private String title;

    /**
    * 父ID
    */
    @ExcelProperty("父ID")
    @ApiModelProperty(value = "父ID")
    @TableField(value = "parent_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer parentId;

    /**
    * 更新时间
    */
    @ExcelProperty("更新时间")
    @ApiModelProperty(value = "更新时间")
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
    * 日期
    */
    @ExcelProperty("日期")
    @ApiModelProperty(value = "日期")
    @TableField(value = "only_date")
    private LocalDate onlyDate;

    /**
    * 支付时间
    */
    @ExcelProperty("支付时间")
    @ApiModelProperty(value = "支付时间")
    @TableField(value = "pay_datetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payDatetime;

    /**
    * 图片
    */
    @ExcelProperty("图片")
    @ApiModelProperty(value = "图片")
    @TableField(value = "img")
    private String img;

}