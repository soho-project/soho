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

@TableName(value ="wallet_recharge")
@Data
public class WalletRecharge implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * code
    */
    @ExcelProperty("code")
    @ApiModelProperty(value = "code")
    @TableField(value = "code")
    private String code;

    /**
    * amount
    */
    @ExcelProperty("amount")
    @ApiModelProperty(value = "amount")
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
    * user_id
    */
    @ExcelProperty("user_id")
    @ApiModelProperty(value = "user_id")
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
    * 支付id
    */
    @ExcelProperty("支付id")
    @ApiModelProperty(value = "支付id")
    @TableField(value = "pay_id")
    private Integer payId;

    /**
    * 充值状态
    */
    @ExcelProperty("充值状态")
    @ApiModelProperty(value = "充值状态")
    @TableField(value = "status")
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