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

@TableName(value ="wallet_type")
@Data
public class WalletType implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 钱包状态
    */
    @ExcelProperty("钱包状态")
    @ApiModelProperty(value = "钱包状态")
    @TableField(value = "status")
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
     * 提现最小金额
     */
    @ExcelProperty("提现最小金额")
    @ApiModelProperty(value = "提现最小金额")
    @TableField(value = "withdrawal_min_amount")
    private BigDecimal withdrawalMinAmount;

    /**
     * 提现手续费费率
     */
    @ExcelProperty("提现手续费费率")
    @ApiModelProperty(value = "提现手续费")
    @TableField(value = "withdrawal_commission_rate")
    private BigDecimal withdrawalCommissionRate;

    /**
     * 提现最小手续费
     */
    @ExcelProperty("提现最小手续费")
    @ApiModelProperty(value = "提现最小手续费")
    @TableField(value = "withdrawal_min_commission")
    private BigDecimal withdrawalMinCommission;


    @ExcelProperty("是否支持提现")
    @ApiModelProperty(value = "是否支持提现")
    @TableField(value = "can_withdrawal")
    private Integer canWithdrawal;

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
     * img_url
     */
    @ExcelProperty("图片地址")
    @ApiModelProperty(value = "图片地址")
    @TableField(value = "img_url")
    private String imgUrl;

    /**
     * rate
     */
    @ExcelProperty("汇率")
    @ApiModelProperty(value = "汇率")
    @TableField(value = "rate")
    private BigDecimal rate;

}