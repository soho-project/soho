package work.soho.wallet.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName(value ="wallet_info")
@Data
public class WalletInfo implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * user_id
    */
    @ExcelProperty("user_id")
    @ApiModelProperty(value = "user_id")
    @TableField(value = "user_id")
    private Long userId;

    /**
    * 钱包类型
    */
    @ExcelProperty("钱包类型")
    @ApiModelProperty(value = "钱包类型")
    @TableField(value = "type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer type;

    /**
    * amount
    */
    @ExcelProperty("amount")
    @ApiModelProperty(value = "amount")
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
    * status
    */
    @ExcelProperty("status")
    @ApiModelProperty(value = "status")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

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