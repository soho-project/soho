package work.soho.shop.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="shop_product_spec_value")
@Data
public class ShopProductSpecValue implements Serializable {
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
    * sku_id
    */
    @ExcelProperty("sku_id")
    @ApiModelProperty(value = "sku_id")
    @TableField(value = "sku_id")
    private Integer skuId;

    /**
    * spec_id
    */
    @ExcelProperty("spec_id")
    @ApiModelProperty(value = "spec_id")
    @TableField(value = "spec_id")
    private Integer specId;

    /**
    * sort_order
    */
    @ExcelProperty("sort_order")
    @ApiModelProperty(value = "sort_order")
    @TableField(value = "sort_order")
    private Integer sortOrder;

    /**
    * value
    */
    @ExcelProperty("value")
    @ApiModelProperty(value = "value")
    @TableField(value = "value")
    private String value;

    /**
    * extend
    */
    @ExcelProperty("extend")
    @ApiModelProperty(value = "extend")
    @TableField(value = "extend")
    private String extend;

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