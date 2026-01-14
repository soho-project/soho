package work.soho.example.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value ="example_option")
@ApiModel("样例选项")
public class ExampleOption implements Serializable {
    /**
    * ID
    */
    @ExcelProperty("ID")
    @ApiModelProperty(value = "ID")
    @TableField(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer id;

    /**
    * key
    */
    @ExcelProperty("key")
    @ApiModelProperty(value = "`key`")
    @TableField(value = "key")
    private String key;

    /**
    * 选项值
    */
    @ExcelProperty("选项值")
    @ApiModelProperty(value = "选项值")
    @TableField(value = "value")
    private String value;

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

}