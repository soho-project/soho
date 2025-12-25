package work.soho.open.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value ="open_app_api")
@Accessors(chain = true)
public class OpenAppApi implements Serializable {
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
    * created_time
    */
    @ExcelProperty("created_time")
    @ApiModelProperty(value = "created_time")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}