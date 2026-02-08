package work.soho.ai.biz.domain;

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
@TableName(value ="ai_app")
@ApiModel("")
public class AiApp implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * 应用唯一编码
    */
    @ExcelProperty("应用唯一编码")
    @ApiModelProperty(value = "应用唯一编码")
    @TableField(value = "code")
    private String code;

    /**
    * 应用名
    */
    @ExcelProperty("应用名")
    @ApiModelProperty(value = "应用名")
    @TableField(value = "title")
    private String title;

    /**
    * 应用描述
    */
    @ExcelProperty("应用描述")
    @ApiModelProperty(value = "应用描述")
    @TableField(value = "description")
    private String description;

    /**
    * system prompt，支持长文本
    */
    @ExcelProperty("system prompt，支持长文本")
    @ApiModelProperty(value = "system prompt，支持长文本")
    @TableField(value = "system_prompt")
    private String systemPrompt;

    /**
    * 状态:0禁用,1启用
    */
    @ExcelProperty("状态:0禁用,1启用")
    @ApiModelProperty(value = "状态:0禁用,1启用")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * provider_id
    */
    @ExcelProperty("provider_id")
    @ApiModelProperty(value = "provider_id")
    @TableField(value = "provider_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long providerId;

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