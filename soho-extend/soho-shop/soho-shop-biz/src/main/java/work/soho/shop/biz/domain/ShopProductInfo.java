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

@TableName(value ="shop_product_info")
@Data
@Accessors(chain = true)
public class ShopProductInfo implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * spu_code
    */
    @ExcelProperty("spu_code")
    @ApiModelProperty(value = "spu_code")
    @TableField(value = "spu_code")
    private String spuCode;

    /**
    * name
    */
    @ExcelProperty("name")
    @ApiModelProperty(value = "name")
    @TableField(value = "name")
    private String name;

    /**
    * sub_title
    */
    @ExcelProperty("sub_title")
    @ApiModelProperty(value = "sub_title")
    @TableField(value = "sub_title")
    private String subTitle;

    /**
    * qty
    */
    @ExcelProperty("qty")
    @ApiModelProperty(value = "qty")
    @TableField(value = "qty")
    private Integer qty;

    /**
    * original_price
    */
    @ExcelProperty("original_price")
    @ApiModelProperty(value = "original_price")
    @TableField(value = "original_price")
    private BigDecimal originalPrice;

    /**
    * selling_price
    */
    @ExcelProperty("selling_price")
    @ApiModelProperty(value = "selling_price")
    @TableField(value = "selling_price")
    private BigDecimal sellingPrice;

    /**
    * shop_id
    */
    @ExcelProperty("shop_id")
    @ApiModelProperty(value = "shop_id")
    @TableField(value = "shop_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer shopId;

    /**
    * main_image
    */
    @ExcelProperty("main_image")
    @ApiModelProperty(value = "main_image")
    @TableField(value = "main_image")
    private String mainImage;

    /**
    * thumbnails
    */
    @ExcelProperty("thumbnails")
    @ApiModelProperty(value = "thumbnails")
    @TableField(value = "thumbnails")
    private String thumbnails;

    /**
    * detail_html
    */
    @ExcelProperty("detail_html")
    @ApiModelProperty(value = "detail_html")
    @TableField(value = "detail_html")
    private String detailHtml;

    /**
    * comment_count
    */
    @ExcelProperty("comment_count")
    @ApiModelProperty(value = "comment_count")
    @TableField(value = "comment_count")
    private Integer commentCount;

    /**
    * category_id
    */
    @ExcelProperty("category_id")
    @ApiModelProperty(value = "category_id")
    @TableField(value = "category_id")
    private String categoryId;

    /**
    * shelf_status
    */
    @ExcelProperty("shelf_status")
    @ApiModelProperty(value = "shelf_status")
    @TableField(value = "shelf_status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer shelfStatus;

    /**
    * audit_status
    */
    @ExcelProperty("audit_status")
    @ApiModelProperty(value = "audit_status")
    @TableField(value = "audit_status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer auditStatus;

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
    private Integer createdTime;

}