package work.soho.wallet.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="wallet_bank_card")
@Data
public class WalletBankCard implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 用户ID
    */
    @ExcelProperty("用户ID")
    @ApiModelProperty(value = "用户ID")
    @TableField(value = "user_id")
    private Long userId;

    /**
    * name
    */
    @ExcelProperty("name")
    @ApiModelProperty(value = "name")
    @TableField(value = "name")
    private String name;

    /**
    * id_code
    */
    @ExcelProperty("id_code")
    @ApiModelProperty(value = "id_code")
    @TableField(value = "id_code")
    private String idCode;

    /**
    * card_code
    */
    @ExcelProperty("card_code")
    @ApiModelProperty(value = "card_code")
    @TableField(value = "card_code")
    private String cardCode;

    /**
    * phone
    */
    @ExcelProperty("phone")
    @ApiModelProperty(value = "phone")
    @TableField(value = "phone")
    private String phone;

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