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
@TableName(value ="shop_freight_calc_log")
public class ShopFreightCalcLog implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * order_id
    */
    @ExcelProperty("order_id")
    @ApiModelProperty(value = "order_id")
    @TableField(value = "order_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;

    /**
    * session_id
    */
    @ExcelProperty("session_id")
    @ApiModelProperty(value = "session_id")
    @TableField(value = "session_id")
    private String sessionId;

    /**
    * region_code
    */
    @ExcelProperty("region_code")
    @ApiModelProperty(value = "region_code")
    @TableField(value = "region_code")
    private String regionCode;

    /**
    * total_amount
    */
    @ExcelProperty("total_amount")
    @ApiModelProperty(value = "total_amount")
    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    /**
    * total_quantity
    */
    @ExcelProperty("total_quantity")
    @ApiModelProperty(value = "total_quantity")
    @TableField(value = "total_quantity")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer totalQuantity;

    /**
    * total_weight
    */
    @ExcelProperty("total_weight")
    @ApiModelProperty(value = "total_weight")
    @TableField(value = "total_weight")
    private BigDecimal totalWeight;

    /**
    * total_volume
    */
    @ExcelProperty("total_volume")
    @ApiModelProperty(value = "total_volume")
    @TableField(value = "total_volume")
    private BigDecimal totalVolume;

    /**
    * calculated_freight
    */
    @ExcelProperty("calculated_freight")
    @ApiModelProperty(value = "calculated_freight")
    @TableField(value = "calculated_freight")
    private BigDecimal calculatedFreight;

    /**
    * template_ids
    */
    @ExcelProperty("template_ids")
    @ApiModelProperty(value = "template_ids")
    @TableField(value = "template_ids")
    private String templateIds;

    /**
    * calc_detail
    */
    @ExcelProperty("calc_detail")
    @ApiModelProperty(value = "calc_detail")
    @TableField(value = "calc_detail")
    private String calcDetail;

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