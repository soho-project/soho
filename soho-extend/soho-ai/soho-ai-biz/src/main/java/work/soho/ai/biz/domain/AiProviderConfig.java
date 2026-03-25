package work.soho.ai.biz.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName(value ="ai_provider_config")
@ApiModel("AI提供方配置表")
public class AiProviderConfig implements Serializable {
    /**
    * API Key
    */
    @ExcelProperty("API Key")
    @ApiModelProperty(value = "API Key")
    @TableField(value = "api_key_ref")
    private String apiKeyRef;

    /**
    * API Base URL
    */
    @ExcelProperty("API Base URL")
    @ApiModelProperty(value = "API Base URL")
    @TableField(value = "base_url")
    private String baseUrl;

    /**
    * 唯一配置编码
    */
    @ExcelProperty("唯一配置编码")
    @ApiModelProperty(value = "唯一配置编码")
    @TableField(value = "code")
    private String code;

    /**
    * 扩展配置
    */
    @ExcelProperty("扩展配置")
    @ApiModelProperty(value = "扩展配置")
    @TableField(value = "config_json")
    private String configJson;

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
    * 默认模型
    */
    @ExcelProperty("默认模型")
    @ApiModelProperty(value = "默认模型")
    @TableField(value = "default_model")
    private String defaultModel;

    /**
    * 支持模型列表
    */
    @ExcelProperty("支持模型列表")
    @ApiModelProperty(value = "支持模型列表")
    @TableField(value = "supported_models")
    private String supportedModels;

    /**
    * 环境(dev/test/prod)
     */
    @ExcelProperty("环境(dev/test/prod)")
    @ApiModelProperty(value = "环境(dev/test/prod)")
    @TableField(value = "env")
    private String env;

    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * 提供方
    */
    @ExcelProperty("提供方")
    @ApiModelProperty(value = "提供方")
    @TableField(value = "provider")
    private String provider;

    /**
    * 服务提供者唯一识别ID（可为空）
    */
    @ExcelProperty("服务提供者唯一识别ID")
    @ApiModelProperty(value = "服务提供者唯一识别ID（可为空）")
    @TableField(value = "provider_unique_id")
    private String providerUniqueId;

    /**
    * 每分钟最大请求数
    */
    @ExcelProperty("每分钟最大请求数")
    @ApiModelProperty(value = "每分钟最大请求数")
    @TableField(value = "rate_limit")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer rateLimit;

    /**
    * 备注说明
    */
    @ExcelProperty("备注说明")
    @ApiModelProperty(value = "备注说明")
    @TableField(value = "remark")
    private String remark;

    /**
    * 状态:0禁用,1启用
    */
    @ExcelProperty("状态:0禁用,1启用")
    @ApiModelProperty(value = "状态:0禁用,1启用")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * 请求超时(毫秒)
    */
    @ExcelProperty("请求超时(毫秒)")
    @ApiModelProperty(value = "请求超时(毫秒)")
    @TableField(value = "timeout_ms")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer timeoutMs;

    /**
    * 路由权重（值越大被选中概率越高）
    */
    @ExcelProperty("路由权重")
    @ApiModelProperty(value = "路由权重（值越大被选中概率越高）")
    @TableField(value = "weight")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer weight;

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
    * 关联模型ID列表
    */
    @ApiModelProperty(value = "关联模型ID列表")
    @TableField(exist = false)
    private List<Long> modelInfoIds;

}
