package work.soho.wallet.biz.domain;

import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@TableName(value ="wallet_withdrawal_order")
@Data
public class WalletWithdrawalOrder implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 提现单编号
     */
    @ExcelProperty("code")
    @ApiModelProperty(value = "code")
    @TableField(value = "code")
    private String code;

    /**
    * 用户ID
    */
    @ExcelProperty("用户ID")
    @ApiModelProperty(value = "用户ID")
    @TableField(value = "user_id")
    private Long userId;

    /**
    * wallet_id
    */
    @ExcelProperty("wallet_id")
    @ApiModelProperty(value = "wallet_id")
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
    * notes
    */
    @ExcelProperty("notes")
    @ApiModelProperty(value = "notes")
    @TableField(value = "notes")
    private String notes;

    /**
    * status
    */
    @ExcelProperty("status")
    @ApiModelProperty(value = "status")
    @TableField(value = "status")
    private Integer status;

    /**
    * admin_id
    */
    @ExcelProperty("admin_id")
    @ApiModelProperty(value = "admin_id")
    @TableField(value = "admin_id")
    private Integer adminId;

    /**
    * admin_notes
    */
    @ExcelProperty("admin_notes")
    @ApiModelProperty(value = "admin_notes")
    @TableField(value = "admin_notes")
    private String adminNotes;

    /**
     * 持卡人
     */
    @ExcelProperty("银行卡持卡人")
    @ApiModelProperty(value = "银行卡持卡人")
    @TableField(value = "card_name")
    private String cardName;

    /**
     * 银行卡号
     */
    @ExcelProperty("银行卡号")
    @ApiModelProperty(value = "银行卡号")
    @TableField(value = "card_code")
    private String cardCode;

    /**
     * 银行卡持卡人手机
     */
    @ExcelProperty("银行卡持卡人手机")
    @ApiModelProperty(value = "银行卡持卡人手机")
    @TableField(value = "card_phone")
    private String cardPhone;

    /**
     * 服务费手续费
     */
    @ExcelProperty("手续费")
    @ApiModelProperty(value = "手续费")
    @TableField(value = "commission_amount")
    BigDecimal commissionAmount;

    /**
     * 实际到账
     */
    @ExcelProperty("实际到账")
    @ApiModelProperty(value = "实际到账")
    @TableField(value = "pay_amount")
    BigDecimal payAmount;

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