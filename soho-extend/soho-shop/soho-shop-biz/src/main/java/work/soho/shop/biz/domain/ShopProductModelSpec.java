package work.soho.shop.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="shop_product_model_spec")
@Data
public class ShopProductModelSpec implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * model_id
    */
    @ExcelProperty("model_id")
    @ApiModelProperty(value = "model_id")
    @TableField(value = "model_id")
    private Integer modelId;

    /**
    * name
    */
    @ExcelProperty("name")
    @ApiModelProperty(value = "name")
    @TableField(value = "name")
    private String name;

    /**
    * sort_order
    */
    @ExcelProperty("sort_order")
    @ApiModelProperty(value = "sort_order")
    @TableField(value = "sort_order")
    private Integer sortOrder;

    /**
    * type
    */
    @ExcelProperty("type")
    @ApiModelProperty(value = "type")
    @TableField(value = "type")
    private Integer type;

    /**
    * extend
    */
    @ExcelProperty("extend")
    @ApiModelProperty(value = "extend")
    @TableField(value = "extend")
    private String extend;

    /**
     * 是否销售属性
     */
    @ExcelProperty("is_sale")
    @ApiModelProperty(value = "is_sale")
    @TableField(value = "is_sale")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer isSale;

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