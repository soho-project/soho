package work.soho.shop.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName(value ="shop_coupon_usage_logs")
@Data
@Accessors(chain = true)
public class ShopCouponUsageLogs implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * user_id
    */
    @ExcelProperty("user_id")
    @ApiModelProperty(value = "user_id")
    @TableField(value = "user_id")
    private Long userId;

    /**
    * coupon_id
    */
    @ExcelProperty("coupon_id")
    @ApiModelProperty(value = "coupon_id")
    @TableField(value = "coupon_id")
    private Long couponId;

    /**
    * order_id
    */
    @ExcelProperty("order_id")
    @ApiModelProperty(value = "order_id")
    @TableField(value = "order_id")
    private Long orderId;

    /**
    * order_amount
    */
    @ExcelProperty("order_amount")
    @ApiModelProperty(value = "order_amount")
    @TableField(value = "order_amount")
    private BigDecimal orderAmount;

    /**
    * discount_amount
    */
    @ExcelProperty("discount_amount")
    @ApiModelProperty(value = "discount_amount")
    @TableField(value = "discount_amount")
    private BigDecimal discountAmount;

    /**
    * used_at
    */
    @ExcelProperty("used_at")
    @ApiModelProperty(value = "used_at")
    @TableField(value = "used_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime usedAt;

}