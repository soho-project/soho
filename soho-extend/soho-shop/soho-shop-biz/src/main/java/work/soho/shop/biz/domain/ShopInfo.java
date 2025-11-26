package work.soho.shop.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="shop_info")
@Data
public class ShopInfo implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * name
    */
    @ExcelProperty("name")
    @ApiModelProperty(value = "name")
    @TableField(value = "name")
    private String name;

    /**
    * keywords
    */
    @ExcelProperty("keywords")
    @ApiModelProperty(value = "keywords")
    @TableField(value = "keywords")
    private String keywords;

    /**
    * Introduction
    */
    @ExcelProperty("Introduction")
    @ApiModelProperty(value = "Introduction")
    @TableField(value = "Introduction")
    private String introduction;

    /**
    * tel
    */
    @ExcelProperty("tel")
    @ApiModelProperty(value = "tel")
    @TableField(value = "tel")
    private String tel;

    /**
    * address
    */
    @ExcelProperty("address")
    @ApiModelProperty(value = "address")
    @TableField(value = "address")
    private String address;

    /**
    * shop_qualifications_imgs
    */
    @ExcelProperty("shop_qualifications_imgs")
    @ApiModelProperty(value = "shop_qualifications_imgs")
    @TableField(value = "shop_qualifications_imgs")
    private String shopQualificationsImgs;

    /**
    * user_id
    */
    @ExcelProperty("user_id")
    @ApiModelProperty(value = "user_id")
    @TableField(value = "user_id")
    private Integer userId;

    /**
    * status
    */
    @ExcelProperty("status")
    @ApiModelProperty(value = "status")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

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