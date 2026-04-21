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
@TableName(value = "ai_api_call_log")
@ApiModel("AI API 调用日志")
public class AiApiCallLog implements Serializable {
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ExcelProperty("request_id")
    @ApiModelProperty(value = "request_id")
    @TableField(value = "request_id")
    private String requestId;

    @ExcelProperty("user_id")
    @ApiModelProperty(value = "user_id")
    @TableField(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    @ExcelProperty("api_key_id")
    @ApiModelProperty(value = "api_key_id")
    @TableField(value = "api_key_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long apiKeyId;

    @ExcelProperty("provider_config_id")
    @ApiModelProperty(value = "provider_config_id")
    @TableField(value = "provider_config_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long providerConfigId;

    @ExcelProperty("endpoint")
    @ApiModelProperty(value = "endpoint")
    @TableField(value = "endpoint")
    private String endpoint;

    @ExcelProperty("model")
    @ApiModelProperty(value = "model")
    @TableField(value = "model")
    private String model;

    @ExcelProperty("request_model")
    @ApiModelProperty(value = "请求模型")
    @TableField(value = "request_model")
    private String requestModel;

    @ExcelProperty("actual_model")
    @ApiModelProperty(value = "实际调用模型")
    @TableField(value = "actual_model")
    private String actualModel;

    @ExcelProperty("prompt_tokens")
    @ApiModelProperty(value = "prompt_tokens")
    @TableField(value = "prompt_tokens")
    private Integer promptTokens;

    @ExcelProperty("completion_tokens")
    @ApiModelProperty(value = "completion_tokens")
    @TableField(value = "completion_tokens")
    private Integer completionTokens;

    @ExcelProperty("total_tokens")
    @ApiModelProperty(value = "total_tokens")
    @TableField(value = "total_tokens")
    private Integer totalTokens;

    @ExcelProperty("amount")
    @ApiModelProperty(value = "amount")
    @TableField(value = "amount")
    private BigDecimal amount;

    @ExcelProperty("status")
    @ApiModelProperty(value = "status")
    @TableField(value = "status")
    private Integer status;

    @ExcelProperty("error_message")
    @ApiModelProperty(value = "error_message")
    @TableField(value = "error_message")
    private String errorMessage;

    @ExcelProperty("wallet_log_id")
    @ApiModelProperty(value = "wallet_log_id")
    @TableField(value = "wallet_log_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long walletLogId;

    @ExcelProperty("total_ms")
    @ApiModelProperty(value = "总耗时(毫秒)")
    @TableField(value = "total_ms")
    private Long totalMs;

    @ExcelProperty("first_token_ms")
    @ApiModelProperty(value = "首字耗时(毫秒)")
    @TableField(value = "first_token_ms")
    private Long firstTokenMs;

    @ExcelProperty("client_ip")
    @ApiModelProperty(value = "客户端IP")
    @TableField(value = "client_ip")
    private String clientIp;

    @ExcelProperty("user_agent")
    @ApiModelProperty(value = "客户端User-Agent")
    @TableField(value = "user_agent")
    private String userAgent;

    @ExcelProperty("request_source")
    @ApiModelProperty(value = "请求来源")
    @TableField(value = "request_source")
    private String requestSource;

    @ExcelProperty("reject_reason")
    @ApiModelProperty(value = "拦截原因")
    @TableField(value = "reject_reason")
    private String rejectReason;

    @ExcelProperty("risk_hit")
    @ApiModelProperty(value = "是否命中风险规则")
    @TableField(value = "risk_hit")
    private Integer riskHit;

    @ExcelProperty("ban_hit")
    @ApiModelProperty(value = "是否命中封禁")
    @TableField(value = "ban_hit")
    private Integer banHit;

    @ExcelProperty("updated_time")
    @ApiModelProperty(value = "updated_time")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @ExcelProperty("created_time")
    @ApiModelProperty(value = "created_time")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}
