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

@TableName(value ="shop_product_category")
@Data
public class ShopProductCategory implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer id;

    /**
    * name
    */
    @ExcelProperty("name")
    @ApiModelProperty(value = "name")
    @TableField(value = "name")
    private String name;

    /**
    * parent_id
    */
    @ExcelProperty("parent_id")
    @ApiModelProperty(value = "parent_id")
    @TableField(value = "parent_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer parentId;

    /**
    * level
    */
    @ExcelProperty("level")
    @ApiModelProperty(value = "level")
    @TableField(value = "level")
    private Integer level;

    /**
    * sort_order
    */
    @ExcelProperty("sort_order")
    @ApiModelProperty(value = "sort_order")
    @TableField(value = "sort_order")
    private Integer sortOrder;

    /**
    * status
    */
    @ExcelProperty("status")
    @ApiModelProperty(value = "status")
    @TableField(value = "status")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;

    /**
    * icon
    */
    @ExcelProperty("icon")
    @ApiModelProperty(value = "icon")
    @TableField(value = "icon")
    private String icon;

    /**
    * description
    */
    @ExcelProperty("description")
    @ApiModelProperty(value = "description")
    @TableField(value = "description")
    private String description;

    /**
     * model_id
     */
    @ExcelProperty("model_id")
    @ApiModelProperty(value = "model_id")
    @TableField(value = "model_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer modelId;

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