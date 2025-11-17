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

@Data
@TableName(value ="wallet_user_order")
public class WalletUserOrder implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * wallet_type
    */
    @ExcelProperty("wallet_type")
    @ApiModelProperty(value = "wallet_type")
    @TableField(value = "wallet_type")
    private Integer walletType;

    /**
    * wallet_id
    */
    @ExcelProperty("wallet_id")
    @ApiModelProperty(value = "wallet_id")
    @TableField(value = "wallet_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer walletId;

    /**
    * no
    */
    @ExcelProperty("no")
    @ApiModelProperty(value = "no")
    @TableField(value = "no")
    private String no;

    /**
    * amount
    */
    @ExcelProperty("amount")
    @ApiModelProperty(value = "amount")
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
    * out_tracking_number
    */
    @ExcelProperty("out_tracking_number")
    @ApiModelProperty(value = "out_tracking_number")
    @TableField(value = "out_tracking_number")
    private String outTrackingNumber;

    /**
    * status
    */
    @ExcelProperty("status")
    @ApiModelProperty(value = "status")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * notes
    */
    @ExcelProperty("notes")
    @ApiModelProperty(value = "notes")
    @TableField(value = "notes")
    private String notes;

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