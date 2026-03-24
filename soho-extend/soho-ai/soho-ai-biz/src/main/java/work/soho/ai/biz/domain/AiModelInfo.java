package work.soho.ai.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName(value = "ai_model_info")
@ApiModel("AI模型信息表")
public class AiModelInfo implements Serializable {
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ExcelProperty("模型名")
    @ApiModelProperty(value = "模型名")
    @TableField(value = "model_name")
    private String modelName;

    @ExcelProperty("模型描述")
    @ApiModelProperty(value = "模型描述")
    @TableField(value = "model_desc")
    private String modelDesc;

    @ExcelProperty("模型详细介绍")
    @ApiModelProperty(value = "模型详细介绍")
    @TableField(value = "model_detail")
    private String modelDetail;

    @ExcelProperty("模型状态")
    @ApiModelProperty(value = "模型状态")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    @ExcelProperty("输入单价(每1K tokens)")
    @ApiModelProperty(value = "输入单价(每1K tokens)")
    @TableField(value = "prompt_price")
    private BigDecimal promptPrice;

    @ExcelProperty("输出单价(每1K tokens)")
    @ApiModelProperty(value = "输出单价(每1K tokens)")
    @TableField(value = "completion_price")
    private BigDecimal completionPrice;

    @ExcelProperty("排序")
    @ApiModelProperty(value = "排序")
    @TableField(value = "sort")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer sort;

    @ExcelProperty("更新时间")
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @ExcelProperty("创建时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}
