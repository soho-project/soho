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

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value ="shop_user_coupons")
public class ShopUserCoupons implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * user_id
    */
    @ExcelProperty("user_id")
    @ApiModelProperty(value = "user_id")
    @TableField(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
    * coupon_id
    */
    @ExcelProperty("coupon_id")
    @ApiModelProperty(value = "coupon_id")
    @TableField(value = "coupon_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long couponId;

    /**
    * coupon_code
    */
    @ExcelProperty("coupon_code")
    @ApiModelProperty(value = "coupon_code")
    @TableField(value = "coupon_code")
    private String couponCode;

    /**
    * status
    */
    @ExcelProperty("status")
    @ApiModelProperty(value = "status")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * used_time
    */
    @ExcelProperty("used_time")
    @ApiModelProperty(value = "used_time")
    @TableField(value = "used_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime usedTime;

    /**
    * order_id
    */
    @ExcelProperty("order_id")
    @ApiModelProperty(value = "order_id")
    @TableField(value = "order_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;

    /**
    * received_at
    */
    @ExcelProperty("received_at")
    @ApiModelProperty(value = "received_at")
    @TableField(value = "received_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receivedAt;

    /**
    * expired_at
    */
    @ExcelProperty("expired_at")
    @ApiModelProperty(value = "expired_at")
    @TableField(value = "expired_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredAt;

}