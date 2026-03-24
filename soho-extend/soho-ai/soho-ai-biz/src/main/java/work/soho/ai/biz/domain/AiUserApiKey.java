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

@Data
@TableName(value = "ai_user_api_key")
@ApiModel("AI 用户调用 Key")
public class AiUserApiKey implements Serializable {
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ExcelProperty("user_id")
    @ApiModelProperty(value = "user_id")
    @TableField(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    @ExcelProperty("name")
    @ApiModelProperty(value = "name")
    @TableField(value = "name")
    private String name;

    @ExcelProperty("api_key_prefix")
    @ApiModelProperty(value = "api_key_prefix")
    @TableField(value = "api_key_prefix")
    private String apiKeyPrefix;

    @ApiModelProperty(value = "api_key_hash")
    @TableField(value = "api_key_hash")
    private String apiKeyHash;

    @ExcelProperty("status")
    @ApiModelProperty(value = "status")
    @TableField(value = "status")
    private Integer status;

    @ExcelProperty("last_used_time")
    @ApiModelProperty(value = "last_used_time")
    @TableField(value = "last_used_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUsedTime;

    @ExcelProperty("updated_time")
    @ApiModelProperty(value = "updated_time")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @ExcelProperty("created_time")
    @ApiModelProperty(value = "created_time")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}
