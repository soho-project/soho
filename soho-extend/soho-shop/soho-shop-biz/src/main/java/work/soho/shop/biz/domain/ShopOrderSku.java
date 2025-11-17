package work.soho.shop.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName(value ="shop_order_sku")
@Data
@Accessors(chain = true)
public class ShopOrderSku implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * order_id
    */
    @ExcelProperty("order_id")
    @ApiModelProperty(value = "order_id")
    @TableField(value = "order_id")
    private Long orderId;

    /**
     * product_id
     */
    @ExcelProperty("product_id")
    @ApiModelProperty(value = "product_id")
    @TableField(value = "product_id")
    private Long productId;

    /**
    * sku_id
    */
    @ExcelProperty("sku_id")
    @ApiModelProperty(value = "sku_id")
    @TableField(value = "sku_id")
    private Integer skuId;

    /**
    * name
    */
    @ExcelProperty("name")
    @ApiModelProperty(value = "name")
    @TableField(value = "name")
    private String name;

    /**
    * specs
    */
    @ExcelProperty("specs")
    @ApiModelProperty(value = "specs")
    @TableField(value = "specs")
    private String specs;

    /**
    * main_image
    */
    @ExcelProperty("main_image")
    @ApiModelProperty(value = "main_image")
    @TableField(value = "main_image")
    private String mainImage;

    /**
    * amount
    */
    @ExcelProperty("amount")
    @ApiModelProperty(value = "amount")
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
    * qty
    */
    @ExcelProperty("qty")
    @ApiModelProperty(value = "qty")
    @TableField(value = "qty")
    private Integer qty;

    /**
    * total_amount
    */
    @ExcelProperty("total_amount")
    @ApiModelProperty(value = "total_amount")
    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

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