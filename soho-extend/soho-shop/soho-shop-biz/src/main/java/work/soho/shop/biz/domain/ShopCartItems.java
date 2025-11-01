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
@TableName(value ="shop_cart_items")
public class ShopCartItems implements Serializable {
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
    private Integer userId;

    /**
    * session_id
    */
    @ExcelProperty("session_id")
    @ApiModelProperty(value = "session_id")
    @TableField(value = "session_id")
    private String sessionId;

    /**
    * product_id
    */
    @ExcelProperty("product_id")
    @ApiModelProperty(value = "product_id")
    @TableField(value = "product_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer productId;

    /**
    * sku_id
    */
    @ExcelProperty("sku_id")
    @ApiModelProperty(value = "sku_id")
    @TableField(value = "sku_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer skuId;

    /**
    * qty
    */
    @ExcelProperty("qty")
    @ApiModelProperty(value = "qty")
    @TableField(value = "qty")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer qty;

    /**
    * is_selected
    */
    @ExcelProperty("is_selected")
    @ApiModelProperty(value = "is_selected")
    @TableField(value = "is_selected")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer isSelected;

    /**
    * price
    */
    @ExcelProperty("price")
    @ApiModelProperty(value = "price")
    @TableField(value = "price")
    private BigDecimal price;

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