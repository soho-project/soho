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
@TableName(value ="open_app_ip_whitelist")
public class OpenAppIpWhitelist implements Serializable {
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
    * IPv4 / IPv6
    */
    @ExcelProperty("IPv4 / IPv6")
    @ApiModelProperty(value = "IPv4 / IPv6")
    @TableField(value = "ip")
    private String ip;

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