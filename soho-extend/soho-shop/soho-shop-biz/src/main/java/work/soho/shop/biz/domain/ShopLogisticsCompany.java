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
@TableName(value ="shop_logistics_company")
public class ShopLogisticsCompany implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * company_code
    */
    @ExcelProperty("company_code")
    @ApiModelProperty(value = "company_code")
    @TableField(value = "company_code")
    private String companyCode;

    /**
    * company_name
    */
    @ExcelProperty("company_name")
    @ApiModelProperty(value = "company_name")
    @TableField(value = "company_name")
    private String companyName;

    /**
    * contact_phone
    */
    @ExcelProperty("contact_phone")
    @ApiModelProperty(value = "contact_phone")
    @TableField(value = "contact_phone")
    private String contactPhone;

    /**
    * is_enabled
    */
    @ExcelProperty("is_enabled")
    @ApiModelProperty(value = "is_enabled")
    @TableField(value = "is_enabled")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer isEnabled;

    /**
    * volumetric_ratio
    */
    @ExcelProperty("volumetric_ratio")
    @ApiModelProperty(value = "volumetric_ratio")
    @TableField(value = "volumetric_ratio")
    private BigDecimal volumetricRatio;

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