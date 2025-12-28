package work.soho.express.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value ="express_info")
public class ExpressInfo implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * 快递名
    */
    @ExcelProperty("快递名")
    @ApiModelProperty(value = "快递名")
    @TableField(value = "name")
    private String name;

    /**
    * 快递类型
    */
    @ExcelProperty("快递类型")
    @ApiModelProperty(value = "快递类型")
    @TableField(value = "express_type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer expressType;

    /**
    * 状态
    */
    @ExcelProperty("状态")
    @ApiModelProperty(value = "状态")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * AppKey
    */
    @ExcelProperty("AppKey")
    @ApiModelProperty(value = "AppKey")
    @TableField(value = "app_key")
    private String appKey;

    /**
    * AppSecret
    */
    @ExcelProperty("AppSecret")
    @ApiModelProperty(value = "AppSecret")
    @TableField(value = "app_secret")
    private String appSecret;

    /**
    * updated_time
    */
    @ExcelProperty("updated_time")
    @ApiModelProperty(value = "updated_time")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

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