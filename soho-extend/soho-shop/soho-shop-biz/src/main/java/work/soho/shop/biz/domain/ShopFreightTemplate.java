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
@TableName(value ="shop_freight_template")
public class ShopFreightTemplate implements Serializable {
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
    * type
    */
    @ExcelProperty("type")
    @ApiModelProperty(value = "type")
    @TableField(value = "type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer type;

    /**
    * valuation_method
    */
    @ExcelProperty("valuation_method")
    @ApiModelProperty(value = "valuation_method")
    @TableField(value = "valuation_method")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer valuationMethod;

    /**
    * is_free_shipping
    */
    @ExcelProperty("is_free_shipping")
    @ApiModelProperty(value = "is_free_shipping")
    @TableField(value = "is_free_shipping")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer isFreeShipping;

    /**
    * free_condition_type
    */
    @ExcelProperty("free_condition_type")
    @ApiModelProperty(value = "free_condition_type")
    @TableField(value = "free_condition_type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer freeConditionType;

    /**
    * free_condition_value
    */
    @ExcelProperty("free_condition_value")
    @ApiModelProperty(value = "free_condition_value")
    @TableField(value = "free_condition_value")
    private BigDecimal freeConditionValue;

    /**
    * include_special_regions
    */
    @ExcelProperty("include_special_regions")
    @ApiModelProperty(value = "include_special_regions")
    @TableField(value = "include_special_regions")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer includeSpecialRegions;

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