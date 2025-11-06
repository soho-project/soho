package work.soho.pay.biz.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="pay_hfpay_wallet")
@Data
public class PayHfpayWallet implements Serializable {
    /**
    * id
    */
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * pay_info_id
    */
    @ApiModelProperty(value = "pay_info_id")
    @TableField(value = "pay_info_id")
    private Integer payInfoId;

    /**
    * order_code
    */
    @ApiModelProperty(value = "order_code")
    @TableField(value = "order_code")
    private String orderCode;

    /**
    * user_id
    */
    @ApiModelProperty(value = "user_id")
    @TableField(value = "user_id")
    private Long userId;

    /**
     * status
     */
    @ApiModelProperty(value = "status")
    @TableField(value = "status")
    private Integer status;

    /**
    * user_cust_id
    */
    @ApiModelProperty(value = "user_cust_id")
    @TableField(value = "user_cust_id")
    private String userCustId;

    /**
    * acct_id
    */
    @ApiModelProperty(value = "acct_id")
    @TableField(value = "acct_id")
    private String acctId;

    /**
    * fee_cust_id
    */
    @ApiModelProperty(value = "fee_cust_id")
    @TableField(value = "fee_cust_id")
    private String feeCustId;

    /**
    * fee_acct_id
    */
    @ApiModelProperty(value = "fee_acct_id")
    @TableField(value = "fee_acct_id")
    private String feeAcctId;

    /**
    * fee_amt
    */
    @ApiModelProperty(value = "fee_amt")
    @TableField(value = "fee_amt")
    private String feeAmt;

    /**
    * id_card
    */
    @ApiModelProperty(value = "id_card")
    @TableField(value = "id_card")
    private String idCard;

    /**
    * id_card_type
    */
    @ApiModelProperty(value = "id_card_type")
    @TableField(value = "id_card_type")
    private String idCardType;

    /**
    * user_name
    */
    @ApiModelProperty(value = "user_name")
    @TableField(value = "user_name")
    private String userName;

    /**
    * user_mobile
    */
    @ApiModelProperty(value = "user_mobile")
    @TableField(value = "user_mobile")
    private String userMobile;

    /**
    * updated_time
    */
    @ApiModelProperty(value = "updated_time")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * created_time
    */
    @ApiModelProperty(value = "created_time")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}