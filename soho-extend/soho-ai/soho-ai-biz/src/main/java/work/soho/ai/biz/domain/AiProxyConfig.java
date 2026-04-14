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
 * AI 代理配置表。
 */
@Data
@TableName(value = "ai_proxy_config")
@ApiModel("AI代理配置表")
public class AiProxyConfig implements Serializable {
    /**
     * 主键ID。
     */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 代理名称。
     */
    @ExcelProperty("代理名称")
    @ApiModelProperty(value = "代理名称")
    @TableField(value = "name")
    private String name;

    /**
     * 绑定供应商。
     */
    @ExcelProperty("绑定供应商")
    @ApiModelProperty(value = "绑定供应商（如 openai/gemini；为空表示全局）")
    @TableField(value = "provider")
    private String provider;

    /**
     * 协议类型。
     */
    @ExcelProperty("协议类型")
    @ApiModelProperty(value = "协议类型（http/https/socks5/ss/vmess/vless/trojan）")
    @TableField(value = "proxy_type")
    private String proxyType;

    /**
     * 代理主机。
     */
    @ExcelProperty("代理主机")
    @ApiModelProperty(value = "代理主机")
    @TableField(value = "proxy_host")
    private String proxyHost;

    /**
     * 代理端口。
     */
    @ExcelProperty("代理端口")
    @ApiModelProperty(value = "代理端口")
    @TableField(value = "proxy_port")
    private Integer proxyPort;

    /**
     * 代理URL。
     */
    @ExcelProperty("代理URL")
    @ApiModelProperty(value = "代理URL，格式 protocol://[user:pass@]host:port")
    @TableField(value = "proxy_url")
    private String proxyUrl;

    /**
     * 代理用户名。
     */
    @ExcelProperty("代理用户名")
    @ApiModelProperty(value = "代理用户名")
    @TableField(value = "proxy_username")
    private String proxyUsername;

    /**
     * 代理密码。
     */
    @ExcelProperty("代理密码")
    @ApiModelProperty(value = "代理密码")
    @TableField(value = "proxy_password")
    private String proxyPassword;

    /**
     * 权重。
     */
    @ExcelProperty("权重")
    @ApiModelProperty(value = "权重（值越大越容易被选中）")
    @TableField(value = "weight")
    private Integer weight;

    /**
     * 状态。
     */
    @ExcelProperty("状态")
    @ApiModelProperty(value = "状态：0禁用，1启用")
    @TableField(value = "status")
    private Integer status;

    /**
     * 备注。
     */
    @ExcelProperty("备注")
    @ApiModelProperty(value = "备注")
    @TableField(value = "remark")
    private String remark;

    /**
     * 更新时间。
     */
    @ExcelProperty("更新时间")
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * 创建时间。
     */
    @ExcelProperty("创建时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}
