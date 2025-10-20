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

@TableName(value ="shop_product_sku")
@Data
@Accessors(chain = true)
public class ShopProductSku implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * product_id
    */
    @ExcelProperty("product_id")
    @ApiModelProperty(value = "product_id")
    @TableField(value = "product_id")
    private Long productId;

    /**
    * code
    */
    @ExcelProperty("code")
    @ApiModelProperty(value = "code")
    @TableField(value = "code")
    private String code;

    /**
    * qty
    */
    @ExcelProperty("qty")
    @ApiModelProperty(value = "qty")
    @TableField(value = "qty")
    private Integer qty;

    /**
    * original_price
    */
    @ExcelProperty("original_price")
    @ApiModelProperty(value = "original_price")
    @TableField(value = "original_price")
    private BigDecimal originalPrice;

    /**
    * selling_price
    */
    @ExcelProperty("selling_price")
    @ApiModelProperty(value = "selling_price")
    @TableField(value = "selling_price")
    private BigDecimal sellingPrice;

    /**
    * main_image
    */
    @ExcelProperty("main_image")
    @ApiModelProperty(value = "main_image")
    @TableField(value = "main_image")
    private String mainImage;

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