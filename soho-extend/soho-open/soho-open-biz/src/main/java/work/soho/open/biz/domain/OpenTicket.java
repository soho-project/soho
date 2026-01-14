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
@TableName(value ="open_ticket")
public class OpenTicket implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * no
    */
    @ExcelProperty("no")
    @ApiModelProperty(value = "no")
    @TableField(value = "no")
    private String no;

    /**
    * category_id
    */
    @ExcelProperty("category_id")
    @ApiModelProperty(value = "category_id")
    @TableField(value = "category_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long categoryId;

    /**
    * title
    */
    @ExcelProperty("title")
    @ApiModelProperty(value = "title")
    @TableField(value = "title")
    private String title;

    /**
    * content
    */
    @ExcelProperty("content")
    @ApiModelProperty(value = "content")
    @TableField(value = "content")
    private String content;

    /**
    * status
    */
    @ExcelProperty("status")
    @ApiModelProperty(value = "status")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * app_id
    */
    @ExcelProperty("app_id")
    @ApiModelProperty(value = "app_id")
    @TableField(value = "app_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    /**
    * last_read_time
    */
    @ExcelProperty("last_read_time")
    @ApiModelProperty(value = "last_read_time")
    @TableField(value = "last_read_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastReadTime;

    /**
    * last_answer_time
    */
    @ExcelProperty("last_answer_time")
    @ApiModelProperty(value = "last_answer_time")
    @TableField(value = "last_answer_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAnswerTime;

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

    /**
    * user_id
    */
    @ExcelProperty("user_id")
    @ApiModelProperty(value = "user_id")
    @TableField(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

}