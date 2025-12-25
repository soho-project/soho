package work.soho.open.biz.domain;

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
@TableName(value ="open_api")
public class OpenApi implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * 接口唯一标识
    */
    @ExcelProperty("接口唯一标识")
    @ApiModelProperty(value = "接口唯一标识")
    @TableField(value = "api_code")
    private String apiCode;

    /**
    * api_name
    */
    @ExcelProperty("api_name")
    @ApiModelProperty(value = "api_name")
    @TableField(value = "api_name")
    private String apiName;

    /**
    * 请求方法
    */
    @ExcelProperty("请求方法")
    @ApiModelProperty(value = "请求方法")
    @TableField(value = "method")
    private String method;

    /**
    * 接口路径
    */
    @ExcelProperty("接口路径")
    @ApiModelProperty(value = "接口路径")
    @TableField(value = "path")
    private String path;

    /**
    * version
    */
    @ExcelProperty("version")
    @ApiModelProperty(value = "version")
    @TableField(value = "version")
    private String version;

    /**
    * 是否需要鉴权
    */
    @ExcelProperty("是否需要鉴权")
    @ApiModelProperty(value = "是否需要鉴权")
    @TableField(value = "need_auth")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer needAuth;

    /**
    * 状态
    */
    @ExcelProperty("状态")
    @ApiModelProperty(value = "状态")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * description
    */
    @ExcelProperty("description")
    @ApiModelProperty(value = "description")
    @TableField(value = "description")
    private String description;

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