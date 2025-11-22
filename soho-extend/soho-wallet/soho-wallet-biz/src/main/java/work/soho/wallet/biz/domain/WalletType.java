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
@TableName(value ="wallet_type")
public class WalletType implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer id;

    /**
    * 钱包状态
    */
    @ExcelProperty("钱包状态")
    @ApiModelProperty(value = "钱包状态")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * name
    */
    @ExcelProperty("name")
    @ApiModelProperty(value = "name")
    @TableField(value = "name")
    private String name;

    /**
    * title
    */
    @ExcelProperty("title")
    @ApiModelProperty(value = "title")
    @TableField(value = "title")
    private String title;

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

    /**
    * withdrawal_min_amount
    */
    @ExcelProperty("withdrawal_min_amount")
    @ApiModelProperty(value = "withdrawal_min_amount")
    @TableField(value = "withdrawal_min_amount")
    private BigDecimal withdrawalMinAmount;

    /**
    * withdrawal_commission_rate
    */
    @ExcelProperty("withdrawal_commission_rate")
    @ApiModelProperty(value = "withdrawal_commission_rate")
    @TableField(value = "withdrawal_commission_rate")
    private BigDecimal withdrawalCommissionRate;

    /**
    * withdrawal_min_commission
    */
    @ExcelProperty("withdrawal_min_commission")
    @ApiModelProperty(value = "withdrawal_min_commission")
    @TableField(value = "withdrawal_min_commission")
    private BigDecimal withdrawalMinCommission;

    /**
    * can_withdrawal
    */
    @ExcelProperty("can_withdrawal")
    @ApiModelProperty(value = "can_withdrawal")
    @TableField(value = "can_withdrawal")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer canWithdrawal;

    /**
    * can_recharge
    */
    @ExcelProperty("can_recharge")
    @ApiModelProperty(value = "can_recharge")
    @TableField(value = "can_recharge")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer canRecharge;

    /**
    * can_transfer_out
    */
    @ExcelProperty("can_transfer_out")
    @ApiModelProperty(value = "can_transfer_out")
    @TableField(value = "can_transfer_out")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer canTransferOut;

    /**
    * can_transfer_in_types
    */
    @ExcelProperty("can_transfer_in_types")
    @ApiModelProperty(value = "can_transfer_in_types")
    @TableField(value = "can_transfer_in_types")
    private String canTransferInTypes;

    /**
    * img_url
    */
    @ExcelProperty("img_url")
    @ApiModelProperty(value = "img_url")
    @TableField(value = "img_url")
    private String imgUrl;

    /**
    * rate
    */
    @ExcelProperty("rate")
    @ApiModelProperty(value = "rate")
    @TableField(value = "rate")
    private BigDecimal rate;

}