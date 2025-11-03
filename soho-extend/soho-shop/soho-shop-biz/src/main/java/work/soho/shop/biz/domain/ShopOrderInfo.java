package work.soho.shop.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName(value ="shop_order_info")
@Data
public class ShopOrderInfo implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer id;

    /**
    * no
    */
    @ExcelProperty("no")
    @ApiModelProperty(value = "no")
    @TableField(value = "no")
    private String no;

    /**
    * user_id
    */
    @ExcelProperty("user_id")
    @ApiModelProperty(value = "user_id")
    @TableField(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
    * amount
    */
    @ExcelProperty("amount")
    @ApiModelProperty(value = "amount")
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
    * product_total_amount
    */
    @ExcelProperty("product_total_amount")
    @ApiModelProperty(value = "product_total_amount")
    @TableField(value = "product_total_amount")
    private BigDecimal productTotalAmount;

    /**
    * delivery_fee
    */
    @ExcelProperty("delivery_fee")
    @ApiModelProperty(value = "delivery_fee")
    @TableField(value = "delivery_fee")
    private BigDecimal deliveryFee;

    /**
    * discount_amount
    */
    @ExcelProperty("discount_amount")
    @ApiModelProperty(value = "discount_amount")
    @TableField(value = "discount_amount")
    private BigDecimal discountAmount;

    /**
    * status
    */
    @ExcelProperty("status")
    @ApiModelProperty(value = "status")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * pay_status
    */
    @ExcelProperty("pay_status")
    @ApiModelProperty(value = "pay_status")
    @TableField(value = "pay_status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer payStatus;

    /**
    * freight_status
    */
    @ExcelProperty("freight_status")
    @ApiModelProperty(value = "freight_status")
    @TableField(value = "freight_status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer freightStatus;

    /**
    * receiving_address
    */
    @ExcelProperty("receiving_address")
    @ApiModelProperty(value = "receiving_address")
    @TableField(value = "receiving_address")
    private String receivingAddress;

    /**
    * consignee
    */
    @ExcelProperty("consignee")
    @ApiModelProperty(value = "consignee")
    @TableField(value = "consignee")
    private String consignee;

    /**
    * receiving_phone_number
    */
    @ExcelProperty("receiving_phone_number")
    @ApiModelProperty(value = "receiving_phone_number")
    @TableField(value = "receiving_phone_number")
    private String receivingPhoneNumber;

    /**
    * order_type
    */
    @ExcelProperty("order_type")
    @ApiModelProperty(value = "order_type")
    @TableField(value = "order_type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer orderType;

    /**
    * source
    */
    @ExcelProperty("source")
    @ApiModelProperty(value = "source")
    @TableField(value = "source")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer source;

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