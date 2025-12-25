package work.soho.open.biz.domain;

import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName(value ="open_app")
public class OpenApp implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * user_id
    */
    @ExcelProperty("user_id")
    @ApiModelProperty(value = "user_id")
    @TableField(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
    * app_name
    */
    @ExcelProperty("app_name")
    @ApiModelProperty(value = "app_name")
    @TableField(value = "app_name")
    private String appName;

    /**
    * app_key
    */
    @ExcelProperty("app_key")
    @ApiModelProperty(value = "app_key")
    @TableField(value = "app_key")
    private String appKey;

    /**
    * app_secret
    */
    @ExcelProperty("app_secret")
    @ApiModelProperty(value = "app_secret")
    @TableField(value = "app_secret")
    private String appSecret;

    /**
    * 1=启用 0=停用
    */
    @ExcelProperty("1=启用 0=停用")
    @ApiModelProperty(value = "1=启用 0=停用")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * 每秒最大请求数
    */
    @ExcelProperty("每秒最大请求数")
    @ApiModelProperty(value = "每秒最大请求数")
    @TableField(value = "qps_limit")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer qpsLimit;

    /**
    * remark
    */
    @ExcelProperty("remark")
    @ApiModelProperty(value = "remark")
    @TableField(value = "remark")
    private String remark;

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