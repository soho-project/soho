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
@TableName(value ="open_ticket_message")
public class OpenTicketMessage implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * TICKET ID
    */
    @ExcelProperty("TICKET ID")
    @ApiModelProperty(value = "TICKET ID")
    @TableField(value = "ticket_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ticketId;

    /**
    * 消息内容
    */
    @ExcelProperty("消息内容")
    @ApiModelProperty(value = "消息内容")
    @TableField(value = "content")
    private String content;

    /**
    * 用户ID
    */
    @ExcelProperty("用户ID")
    @ApiModelProperty(value = "用户ID")
    @TableField(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
    * 消息类型
    */
    @ExcelProperty("消息类型")
    @ApiModelProperty(value = "消息类型")
    @TableField(value = "type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer type;

    /**
    * 管理员ID
    */
    @ExcelProperty("管理员ID")
    @ApiModelProperty(value = "管理员ID")
    @TableField(value = "admin_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long adminId;

    /**
    * 更新时间
    */
    @ExcelProperty("更新时间")
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ExcelProperty("创建时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}