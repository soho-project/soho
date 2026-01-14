package work.soho.open.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value ="open_auth_apply_records")
public class OpenAuthApplyRecords implements Serializable {
    /**
    * ID
    */
    @ExcelProperty("ID")
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * 申请流水号
    */
    @ExcelProperty("申请流水号")
    @ApiModelProperty(value = "申请流水号")
    @TableField(value = "apply_no")
    private String applyNo;

    /**
    * 用户ID
    */
    @ExcelProperty("用户ID")
    @ApiModelProperty(value = "用户ID")
    @TableField(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
    * 应用ID
    */
    @ExcelProperty("应用ID")
    @ApiModelProperty(value = "应用ID")
    @TableField(value = "app_id")
    private String appId;

    /**
    * 认证类型
    */
    @ExcelProperty("认证类型")
    @ApiModelProperty(value = "认证类型")
    @TableField(value = "auth_type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer authType;

    /**
    * 申请状态
    */
    @ExcelProperty("申请状态")
    @ApiModelProperty(value = "申请状态")
    @TableField(value = "apply_status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer applyStatus;

    /**
    * 失败原因
    */
    @ExcelProperty("失败原因")
    @ApiModelProperty(value = "失败原因")
    @TableField(value = "fail_reason")
    private String failReason;

    /**
    * 审核员ID
    */
    @ExcelProperty("审核员ID")
    @ApiModelProperty(value = "审核员ID")
    @TableField(value = "admin_id")
    private Long adminId;

    /**
    * 审核时间
    */
    @ExcelProperty("审核时间")
    @ApiModelProperty(value = "审核时间")
    @TableField(value = "audit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;

    /**
    * 审核备注
    */
    @ExcelProperty("审核备注")
    @ApiModelProperty(value = "审核备注")
    @TableField(value = "audit_remark")
    private String auditRemark;

    /**
    * 来源IP
    */
    @ExcelProperty("来源IP")
    @ApiModelProperty(value = "来源IP")
    @TableField(value = "source_ip")
    private String sourceIp;

    /**
    * 用户代理
    */
    @ExcelProperty("用户代理")
    @ApiModelProperty(value = "用户代理")
    @TableField(value = "user_agent")
    private String userAgent;

    /**
    * 请求原始数据
    */
    @ExcelProperty("请求原始数据")
    @ApiModelProperty(value = "请求原始数据")
    @TableField(value = "request_data")
    private String requestData;

    /**
    * 响应数据
    */
    @ExcelProperty("响应数据")
    @ApiModelProperty(value = "响应数据")
    @TableField(value = "response_data")
    private String responseData;

    /**
    * 回调地址
    */
    @ExcelProperty("回调地址")
    @ApiModelProperty(value = "回调地址")
    @TableField(value = "callback_url")
    private String callbackUrl;

    /**
    * 回调状态
    */
    @ExcelProperty("回调状态")
    @ApiModelProperty(value = "回调状态")
    @TableField(value = "callback_status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer callbackStatus;

    /**
    * 回调时间
    */
    @ExcelProperty("回调时间")
    @ApiModelProperty(value = "回调时间")
    @TableField(value = "callback_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime callbackTime;

    /**
    * 重试次数
    */
    @ExcelProperty("重试次数")
    @ApiModelProperty(value = "重试次数")
    @TableField(value = "retry_count")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer retryCount;

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

}