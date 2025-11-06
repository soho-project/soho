package work.soho.wallet.biz.domain;

import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import java.math.BigDecimal;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="wallet_transfer")
@Data
public class WalletTransfer implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * code
    */
    @ExcelProperty("code")
    @ApiModelProperty(value = "code")
    @TableField(value = "code")
    private String code;

    /**
    * from_user_id
    */
    @ExcelProperty("from_user_id")
    @ApiModelProperty(value = "from_user_id")
    @TableField(value = "from_user_id")
    private Long fromUserId;

    /**
    * from_wallet_id
    */
    @ExcelProperty("from_wallet_id")
    @ApiModelProperty(value = "from_wallet_id")
    @TableField(value = "from_wallet_id")
    private Long fromWalletId;

    /**
    * 来源钱包类型
    */
    @ExcelProperty("来源钱包类型")
    @ApiModelProperty(value = "来源钱包类型")
    @TableField(value = "from_wallet_type")
    private Integer fromWalletType;

    /**
    * from_amount
    */
    @ExcelProperty("from_amount")
    @ApiModelProperty(value = "from_amount")
    @TableField(value = "from_amount")
    private BigDecimal fromAmount;

    /**
    * to_wallet_id
    */
    @ExcelProperty("to_wallet_id")
    @ApiModelProperty(value = "to_wallet_id")
    @TableField(value = "to_wallet_id")
    private Long toWalletId;

    /**
    * to_wallet_type
    */
    @ExcelProperty("to_wallet_type")
    @ApiModelProperty(value = "to_wallet_type")
    @TableField(value = "to_wallet_type")
    private Integer toWalletType;

    /**
    * to_user_id
    */
    @ExcelProperty("to_user_id")
    @ApiModelProperty(value = "to_user_id")
    @TableField(value = "to_user_id")
    private Long toUserId;

    /**
    * to_amount
    */
    @ExcelProperty("to_amount")
    @ApiModelProperty(value = "to_amount")
    @TableField(value = "to_amount")
    private BigDecimal toAmount;

    /**
     * 实际到账金额
     */
    @ExcelProperty("实际转账金额")
    @ApiModelProperty(value = "from_pay_amount")
    @TableField(value = "from_pay_amount")
    private BigDecimal fromPayAmount;

    /**
     * 手续费
     */
    @ExcelProperty("手续费")
    @ApiModelProperty(value = "手续费")
    @TableField(value = "from_commission_amount")
    private BigDecimal fromCommissionAmount;

    /**
    * remark
    */
    @ExcelProperty("remark")
    @ApiModelProperty(value = "remark")
    @TableField(value = "remark")
    private String remark;

    /**
    * updated_time
    */
    @ExcelProperty("updated_time")
    @ApiModelProperty(value = "updated_time")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
    * created_time
    */
    @ExcelProperty("created_time")
    @ApiModelProperty(value = "created_time")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

}