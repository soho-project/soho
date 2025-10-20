package work.soho.shop.biz.domain;

import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName(value ="shop_coupon_apply_ranges")
public class ShopCouponApplyRanges implements Serializable {
    /**
    * scope_name
    */
    @ExcelProperty("scope_name")
    @ApiModelProperty(value = "scope_name")
    @TableField(value = "scope_name")
    private String scopeName;

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
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * coupon_id
    */
    @ExcelProperty("coupon_id")
    @ApiModelProperty(value = "coupon_id")
    @TableField(value = "coupon_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long couponId;

    /**
    * shop_id
    */
    @ExcelProperty("shop_id")
    @ApiModelProperty(value = "shop_id")
    @TableField(value = "shop_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer shopId;

    /**
    * scope_type
    */
    @ExcelProperty("scope_type")
    @ApiModelProperty(value = "scope_type")
    @TableField(value = "scope_type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer scopeType;

    /**
    * scope_id
    */
    @ExcelProperty("scope_id")
    @ApiModelProperty(value = "scope_id")
    @TableField(value = "scope_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long scopeId;

}