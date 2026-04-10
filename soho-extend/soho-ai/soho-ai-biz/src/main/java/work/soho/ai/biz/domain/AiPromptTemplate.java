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
import java.time.LocalDateTime;

/**
 * AI 提示词模板。
 */
@Data
@TableName("ai_prompt_template")
@ApiModel("AI提示词模板")
public class AiPromptTemplate implements Serializable {
    @ExcelProperty("id")
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ExcelProperty("模板编码")
    @ApiModelProperty("模板编码")
    @TableField("code")
    private String code;

    @ExcelProperty("模板名称")
    @ApiModelProperty("模板名称")
    @TableField("name")
    private String name;

    @ExcelProperty("场景编码")
    @ApiModelProperty("场景编码")
    @TableField("scene_code")
    private String sceneCode;

    @ExcelProperty("系统提示词模板")
    @ApiModelProperty("系统提示词模板")
    @TableField("system_prompt")
    private String systemPrompt;

    @ExcelProperty("用户提示词模板")
    @ApiModelProperty("用户提示词模板")
    @TableField("user_prompt_template")
    private String userPromptTemplate;

    @ExcelProperty("说明")
    @ApiModelProperty("说明")
    @TableField("description")
    private String description;

    @ExcelProperty("提供方编码")
    @ApiModelProperty("提供方编码")
    @TableField("provider_code")
    private String providerCode;

    @ExcelProperty("模型匹配")
    @ApiModelProperty("模型匹配")
    @TableField("model_pattern")
    private String modelPattern;

    @ExcelProperty("版本号")
    @ApiModelProperty("版本号")
    @TableField("version")
    private Integer version;

    @ExcelProperty("状态")
    @ApiModelProperty("状态: 0草稿 1发布")
    @TableField("status")
    private Integer status;

    @ExcelProperty("更新时间")
    @ApiModelProperty("更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @ExcelProperty("创建时间")
    @ApiModelProperty("创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}
