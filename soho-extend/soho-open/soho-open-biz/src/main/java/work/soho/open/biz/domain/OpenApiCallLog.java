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
@TableName(value ="open_api_call_log")
public class OpenApiCallLog implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * app_id
    */
    @ExcelProperty("app_id")
    @ApiModelProperty(value = "app_id")
    @TableField(value = "app_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    /**
    * api_id
    */
    @ExcelProperty("api_id")
    @ApiModelProperty(value = "api_id")
    @TableField(value = "api_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long apiId;

    /**
    * request_id
    */
    @ExcelProperty("request_id")
    @ApiModelProperty(value = "request_id")
    @TableField(value = "request_id")
    private String requestId;

    /**
    * client_ip
    */
    @ExcelProperty("client_ip")
    @ApiModelProperty(value = "client_ip")
    @TableField(value = "client_ip")
    private String clientIp;

    /**
    * response_code
    */
    @ExcelProperty("response_code")
    @ApiModelProperty(value = "response_code")
    @TableField(value = "response_code")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer responseCode;

    /**
    * cost_ms
    */
    @ExcelProperty("cost_ms")
    @ApiModelProperty(value = "cost_ms")
    @TableField(value = "cost_ms")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long costMs;

    /**
    * created_time
    */
    @ExcelProperty("created_time")
    @ApiModelProperty(value = "created_time")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}