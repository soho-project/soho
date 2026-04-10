package work.soho.ai.biz.domain;

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
 * AI 提示词渲染日志。
 */
@Data
@TableName("ai_prompt_render_log")
@ApiModel("AI提示词渲染日志")
public class AiPromptRenderLog implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty("请求ID")
    @TableField("request_id")
    private String requestId;

    @ApiModelProperty("用户ID")
    @TableField("user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    @ApiModelProperty("会话ID")
    @TableField("session_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long sessionId;

    @ApiModelProperty("提供方编码")
    @TableField("provider_code")
    private String providerCode;

    @ApiModelProperty("模型")
    @TableField("model")
    private String model;

    @ApiModelProperty("场景编码")
    @TableField("scene_code")
    private String sceneCode;

    @ApiModelProperty("模板ID")
    @TableField("template_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long templateId;

    @ApiModelProperty("模板编码")
    @TableField("template_code")
    private String templateCode;

    @ApiModelProperty("模板版本")
    @TableField("template_version")
    private Integer templateVersion;

    @ApiModelProperty("变量快照")
    @TableField("prompt_vars_json")
    private String promptVarsJson;

    @ApiModelProperty("渲染后的系统提示词")
    @TableField("rendered_instructions")
    private String renderedInstructions;

    @ApiModelProperty("渲染后的用户输入")
    @TableField("rendered_input")
    private String renderedInput;

    @ApiModelProperty("更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @ApiModelProperty("创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}
