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
@TableName(value ="shop_freight_rule")
public class ShopFreightRule implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * template_id
    */
    @ExcelProperty("template_id")
    @ApiModelProperty(value = "template_id")
    @TableField(value = "template_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long templateId;

    /**
    * region_codes
    */
    @ExcelProperty("region_codes")
    @ApiModelProperty(value = "region_codes")
    @TableField(value = "region_codes")
    private String regionCodes;

    /**
    * first_unit
    */
    @ExcelProperty("first_unit")
    @ApiModelProperty(value = "first_unit")
    @TableField(value = "first_unit")
    private BigDecimal firstUnit;

    /**
    * first_unit_price
    */
    @ExcelProperty("first_unit_price")
    @ApiModelProperty(value = "first_unit_price")
    @TableField(value = "first_unit_price")
    private BigDecimal firstUnitPrice;

    /**
    * continue_unit
    */
    @ExcelProperty("continue_unit")
    @ApiModelProperty(value = "continue_unit")
    @TableField(value = "continue_unit")
    private BigDecimal continueUnit;

    /**
    * continue_unit_price
    */
    @ExcelProperty("continue_unit_price")
    @ApiModelProperty(value = "continue_unit_price")
    @TableField(value = "continue_unit_price")
    private BigDecimal continueUnitPrice;

    /**
    * is_default
    */
    @ExcelProperty("is_default")
    @ApiModelProperty(value = "is_default")
    @TableField(value = "is_default")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer isDefault;

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