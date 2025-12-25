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
@TableName(value ="open_api_stat_day")
public class OpenApiStatDay implements Serializable {
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
    * stat_date
    */
    @ExcelProperty("stat_date")
    @ApiModelProperty(value = "stat_date")
    @TableField(value = "stat_date")
    private LocalDate statDate;

    /**
    * call_count
    */
    @ExcelProperty("call_count")
    @ApiModelProperty(value = "call_count")
    @TableField(value = "call_count")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer callCount;

    /**
    * fail_count
    */
    @ExcelProperty("fail_count")
    @ApiModelProperty(value = "fail_count")
    @TableField(value = "fail_count")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer failCount;

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