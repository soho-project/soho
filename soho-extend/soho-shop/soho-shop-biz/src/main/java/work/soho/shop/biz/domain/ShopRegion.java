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
@TableName(value ="shop_region")
public class ShopRegion implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * region_code
    */
    @ExcelProperty("region_code")
    @ApiModelProperty(value = "region_code")
    @TableField(value = "region_code")
    private String regionCode;

    /**
    * region_name
    */
    @ExcelProperty("region_name")
    @ApiModelProperty(value = "region_name")
    @TableField(value = "region_name")
    private String regionName;

    /**
    * parent_code
    */
    @ExcelProperty("parent_code")
    @ApiModelProperty(value = "parent_code")
    @TableField(value = "parent_code")
    private String parentCode;

    /**
    * region_level
    */
    @ExcelProperty("region_level")
    @ApiModelProperty(value = "region_level")
    @TableField(value = "region_level")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer regionLevel;

    /**
    * is_remote
    */
    @ExcelProperty("is_remote")
    @ApiModelProperty(value = "is_remote")
    @TableField(value = "is_remote")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer isRemote;

    /**
    * sort_order
    */
    @ExcelProperty("sort_order")
    @ApiModelProperty(value = "sort_order")
    @TableField(value = "sort_order")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer sortOrder;

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