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

@TableName(value ="wallet_log")
@Data
public class WalletLog implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 钱包ID
    */
    @ExcelProperty("钱包ID")
    @ApiModelProperty(value = "钱包ID")
    @TableField(value = "wallet_id")
    private Long walletId;

    /**
    * amount
    */
    @ExcelProperty("amount")
    @ApiModelProperty(value = "amount")
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
    * before_amount
    */
    @ExcelProperty("before_amount")
    @ApiModelProperty(value = "before_amount")
    @TableField(value = "before_amount")
    private BigDecimal beforeAmount;

    /**
    * after_amount
    */
    @ExcelProperty("after_amount")
    @ApiModelProperty(value = "after_amount")
    @TableField(value = "after_amount")
    private BigDecimal afterAmount;

    /**
    * notes
    */
    @ExcelProperty("notes")
    @ApiModelProperty(value = "notes")
    @TableField(value = "notes")
    private String notes;

    /**
     * 业务ID
     */
    @ExcelProperty("biz_id")
    @ApiModelProperty(value = "biz_id")
    @TableField(value = "biz_id")
    private Integer bizId;

    /**
     * 外部跟踪ID
     */
    @ExcelProperty("out_track_id")
    @ApiModelProperty(value = "out_track_id")
    @TableField(value = "out_track_id")
    private String outTrackId;

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