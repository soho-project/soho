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
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName(value ="shop_user_addresses")
@Data
public class ShopUserAddresses implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * user_id
    */
    @ExcelProperty("user_id")
    @ApiModelProperty(value = "user_id")
    @TableField(value = "user_id")
    private Integer userId;

    /**
    * recipient_name
    */
    @ExcelProperty("recipient_name")
    @ApiModelProperty(value = "recipient_name")
    @TableField(value = "recipient_name")
    private String recipientName;

    /**
    * recipient_phone
    */
    @ExcelProperty("recipient_phone")
    @ApiModelProperty(value = "recipient_phone")
    @TableField(value = "recipient_phone")
    private String recipientPhone;

    /**
    * country
    */
    @ExcelProperty("country")
    @ApiModelProperty(value = "country")
    @TableField(value = "country")
    private String country;

    /**
    * province
    */
    @ExcelProperty("province")
    @ApiModelProperty(value = "province")
    @TableField(value = "province")
    private String province;

    /**
    * city
    */
    @ExcelProperty("city")
    @ApiModelProperty(value = "city")
    @TableField(value = "city")
    private String city;

    /**
    * district
    */
    @ExcelProperty("district")
    @ApiModelProperty(value = "district")
    @TableField(value = "district")
    private String district;

    /**
    * postal_code
    */
    @ExcelProperty("postal_code")
    @ApiModelProperty(value = "postal_code")
    @TableField(value = "postal_code")
    private String postalCode;

    /**
    * detail_address
    */
    @ExcelProperty("detail_address")
    @ApiModelProperty(value = "detail_address")
    @TableField(value = "detail_address")
    private String detailAddress;

    /**
    * is_default
    */
    @ExcelProperty("is_default")
    @ApiModelProperty(value = "is_default")
    @TableField(value = "is_default")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer isDefault;

    /**
    * address_tags
    */
    @ExcelProperty("address_tags")
    @ApiModelProperty(value = "address_tags")
    @TableField(value = "address_tags")
    private String addressTags;

    /**
    * latitude
    */
    @ExcelProperty("latitude")
    @ApiModelProperty(value = "latitude")
    @TableField(value = "latitude")
    private BigDecimal latitude;

    /**
    * longitude
    */
    @ExcelProperty("longitude")
    @ApiModelProperty(value = "longitude")
    @TableField(value = "longitude")
    private BigDecimal longitude;

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

    /**
    * is_deleted
    */
    @ExcelProperty("is_deleted")
    @ApiModelProperty(value = "is_deleted")
    @TableField(value = "is_deleted")
    private Integer isDeleted;

}