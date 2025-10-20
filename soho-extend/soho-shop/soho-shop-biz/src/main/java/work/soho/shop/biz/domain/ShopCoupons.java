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

@TableName(value ="shop_coupons")
@Data
public class ShopCoupons implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * shop_id
    */
    @ExcelProperty("shop_id")
    @ApiModelProperty(value = "shop_id")
    @TableField(value = "shop_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer shopId;

    /**
    * name
    */
    @ExcelProperty("name")
    @ApiModelProperty(value = "name")
    @TableField(value = "name")
    private String name;

    /**
    * code
    */
    @ExcelProperty("code")
    @ApiModelProperty(value = "code")
    @TableField(value = "code")
    private String code;

    /**
    * type
    */
    @ExcelProperty("type")
    @ApiModelProperty(value = "type")
    @TableField(value = "type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer type;

    /**
    * description
    */
    @ExcelProperty("description")
    @ApiModelProperty(value = "description")
    @TableField(value = "description")
    private String description;

    /**
    * min_order_amount
    */
    @ExcelProperty("min_order_amount")
    @ApiModelProperty(value = "min_order_amount")
    @TableField(value = "min_order_amount")
    private BigDecimal minOrderAmount;

    /**
    * discount_value
    */
    @ExcelProperty("discount_value")
    @ApiModelProperty(value = "discount_value")
    @TableField(value = "discount_value")
    private BigDecimal discountValue;

    /**
    * max_discount_amount
    */
    @ExcelProperty("max_discount_amount")
    @ApiModelProperty(value = "max_discount_amount")
    @TableField(value = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    /**
    * total_quantity
    */
    @ExcelProperty("total_quantity")
    @ApiModelProperty(value = "total_quantity")
    @TableField(value = "total_quantity")
    private Integer totalQuantity;

    /**
    * used_quantity
    */
    @ExcelProperty("used_quantity")
    @ApiModelProperty(value = "used_quantity")
    @TableField(value = "used_quantity")
    private Integer usedQuantity;

    /**
    * limit_per_user
    */
    @ExcelProperty("limit_per_user")
    @ApiModelProperty(value = "limit_per_user")
    @TableField(value = "limit_per_user")
    private Integer limitPerUser;

    /**
    * valid_from
    */
    @ExcelProperty("valid_from")
    @ApiModelProperty(value = "valid_from")
    @TableField(value = "valid_from")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validFrom;

    /**
    * valid_to
    */
    @ExcelProperty("valid_to")
    @ApiModelProperty(value = "valid_to")
    @TableField(value = "valid_to")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validTo;

    /**
    * status
    */
    @ExcelProperty("status")
    @ApiModelProperty(value = "status")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * apply_scope
    */
    @ExcelProperty("apply_scope")
    @ApiModelProperty(value = "apply_scope")
    @TableField(value = "apply_scope")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer applyScope;

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
    * updated_time
    */
    @ExcelProperty("updated_time")
    @ApiModelProperty(value = "updated_time")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

}