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
@TableName(value ="shop_user_reviews")
public class ShopUserReviews implements Serializable {
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
    * user_id
    */
    @ExcelProperty("user_id")
    @ApiModelProperty(value = "user_id")
    @TableField(value = "user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    /**
    * order_id
    */
    @ExcelProperty("order_id")
    @ApiModelProperty(value = "order_id")
    @TableField(value = "order_id")
    private String orderId;

    /**
    * parent_id
    */
    @ExcelProperty("parent_id")
    @ApiModelProperty(value = "parent_id")
    @TableField(value = "parent_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    /**
    * rating
    */
    @ExcelProperty("rating")
    @ApiModelProperty(value = "rating")
    @TableField(value = "rating")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer rating;

    /**
    * title
    */
    @ExcelProperty("title")
    @ApiModelProperty(value = "title")
    @TableField(value = "title")
    private String title;

    /**
    * content
    */
    @ExcelProperty("content")
    @ApiModelProperty(value = "content")
    @TableField(value = "content")
    private String content;

    /**
    * content_html
    */
    @ExcelProperty("content_html")
    @ApiModelProperty(value = "content_html")
    @TableField(value = "content_html")
    private String contentHtml;

    /**
    * media_urls
    */
    @ExcelProperty("media_urls")
    @ApiModelProperty(value = "media_urls")
    @TableField(value = "media_urls")
    private String mediaUrls;

    /**
    * is_anonymous
    */
    @ExcelProperty("is_anonymous")
    @ApiModelProperty(value = "is_anonymous")
    @TableField(value = "is_anonymous")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer isAnonymous;

    /**
    * like_count
    */
    @ExcelProperty("like_count")
    @ApiModelProperty(value = "like_count")
    @TableField(value = "like_count")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer likeCount;

    /**
    * report_count
    */
    @ExcelProperty("report_count")
    @ApiModelProperty(value = "report_count")
    @TableField(value = "report_count")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer reportCount;

    /**
    * 审核状态
    */
    @ExcelProperty("审核状态")
    @ApiModelProperty(value = "审核状态")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * audit_admin_id
    */
    @ExcelProperty("audit_admin_id")
    @ApiModelProperty(value = "audit_admin_id")
    @TableField(value = "audit_admin_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long auditAdminId;

    /**
    * audit_remark
    */
    @ExcelProperty("audit_remark")
    @ApiModelProperty(value = "audit_remark")
    @TableField(value = "audit_remark")
    private String auditRemark;

    /**
    * audit_time
    */
    @ExcelProperty("audit_time")
    @ApiModelProperty(value = "audit_time")
    @TableField(value = "audit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;

    /**
    * is_top
    */
    @ExcelProperty("is_top")
    @ApiModelProperty(value = "is_top")
    @TableField(value = "is_top")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer isTop;

    /**
    * extra_data
    */
    @ExcelProperty("extra_data")
    @ApiModelProperty(value = "extra_data")
    @TableField(value = "extra_data")
    private String extraData;

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