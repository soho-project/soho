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

@Data
@Accessors(chain = true)
@TableName(value ="shop_product_freight")
public class ShopProductFreight implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * product_id
    */
    @ExcelProperty("product_id")
    @ApiModelProperty(value = "product_id")
    @TableField(value = "product_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long productId;

    /**
    * template_id
    */
    @ExcelProperty("template_id")
    @ApiModelProperty(value = "template_id")
    @TableField(value = "template_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long templateId;

    /**
    * weight
    */
    @ExcelProperty("weight")
    @ApiModelProperty(value = "weight")
    @TableField(value = "weight")
    private BigDecimal weight;

    /**
    * length
    */
    @ExcelProperty("length")
    @ApiModelProperty(value = "length")
    @TableField(value = "length")
    private BigDecimal length;

    /**
    * width
    */
    @ExcelProperty("width")
    @ApiModelProperty(value = "width")
    @TableField(value = "width")
    private BigDecimal width;

    /**
    * height
    */
    @ExcelProperty("height")
    @ApiModelProperty(value = "height")
    @TableField(value = "height")
    private BigDecimal height;

    /**
    * volumetric_weight
    */
    @ExcelProperty("volumetric_weight")
    @ApiModelProperty(value = "volumetric_weight")
    @TableField(value = "volumetric_weight")
    private BigDecimal volumetricWeight;

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