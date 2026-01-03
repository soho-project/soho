package work.soho.express.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value ="express_order")
public class ExpressOrder implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * 单号
    */
    @ExcelProperty("单号")
    @ApiModelProperty(value = "单号")
    @TableField(value = "no")
    private String no;

    /**
    * 状态
    */
    @ExcelProperty("状态")
    @ApiModelProperty(value = "状态")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * sender_name
    */
    @ExcelProperty("sender_name")
    @ApiModelProperty(value = "sender_name")
    @TableField(value = "sender_name")
    private String senderName;

    /**
    * sender_phone
    */
    @ExcelProperty("sender_phone")
    @ApiModelProperty(value = "sender_phone")
    @TableField(value = "sender_phone")
    private String senderPhone;

    /**
    * sender_mobile
    */
    @ExcelProperty("sender_mobile")
    @ApiModelProperty(value = "sender_mobile")
    @TableField(value = "sender_mobile")
    private String senderMobile;

    /**
    * sender_province
    */
    @ExcelProperty("sender_province")
    @ApiModelProperty(value = "sender_province")
    @TableField(value = "sender_province")
    private String senderProvince;

    /**
    * sender_city
    */
    @ExcelProperty("sender_city")
    @ApiModelProperty(value = "sender_city")
    @TableField(value = "sender_city")
    private String senderCity;

    /**
    * sender_district
    */
    @ExcelProperty("sender_district")
    @ApiModelProperty(value = "sender_district")
    @TableField(value = "sender_district")
    private String senderDistrict;

    /**
    * sender_address
    */
    @ExcelProperty("sender_address")
    @ApiModelProperty(value = "sender_address")
    @TableField(value = "sender_address")
    private String senderAddress;

    /**
    * receiver_name
    */
    @ExcelProperty("receiver_name")
    @ApiModelProperty(value = "receiver_name")
    @TableField(value = "receiver_name")
    private String receiverName;

    /**
    * receiver_phone
    */
    @ExcelProperty("receiver_phone")
    @ApiModelProperty(value = "receiver_phone")
    @TableField(value = "receiver_phone")
    private String receiverPhone;

    /**
    * receiver_mobile
    */
    @ExcelProperty("receiver_mobile")
    @ApiModelProperty(value = "receiver_mobile")
    @TableField(value = "receiver_mobile")
    private String receiverMobile;

    /**
    * receiver_province
    */
    @ExcelProperty("receiver_province")
    @ApiModelProperty(value = "receiver_province")
    @TableField(value = "receiver_province")
    private String receiverProvince;

    /**
    * receiver_city
    */
    @ExcelProperty("receiver_city")
    @ApiModelProperty(value = "receiver_city")
    @TableField(value = "receiver_city")
    private String receiverCity;

    /**
    * receiver_district
    */
    @ExcelProperty("receiver_district")
    @ApiModelProperty(value = "receiver_district")
    @TableField(value = "receiver_district")
    private String receiverDistrict;

    /**
    * receiver_address
    */
    @ExcelProperty("receiver_address")
    @ApiModelProperty(value = "receiver_address")
    @TableField(value = "receiver_address")
    private String receiverAddress;

    /**
    * 物品详情
    */
    @ExcelProperty("物品详情")
    @ApiModelProperty(value = "物品详情")
    @TableField(value = "order_items")
    private String orderItems;

    /**
    * summary_info
    */
    @ExcelProperty("summary_info")
    @ApiModelProperty(value = "summary_info")
    @TableField(value = "summary_info")
    private String summaryInfo;

    /**
    * order_vas_list
    */
    @ExcelProperty("order_vas_list")
    @ApiModelProperty(value = "order_vas_list")
    @TableField(value = "order_vas_list")
    private String orderVasList;

    /**
    * 增值服务信息
    */
    @ExcelProperty("增值服务信息")
    @ApiModelProperty(value = "增值服务信息")
    @TableField(value = "remark")
    private String remark;

    /**
    * 快递单号
    */
    @ExcelProperty("快递单号")
    @ApiModelProperty(value = "快递单号")
    @TableField(value = "bill_code")
    private String billCode;

    /**
    * 商家订单号
    */
    @ExcelProperty("商家订单号")
    @ApiModelProperty(value = "商家订单号")
    @TableField(value = "partner_order_no")
    private String partnerOrderNo;

    /**
    * 快递信息id
    */
    @ExcelProperty("快递信息id")
    @ApiModelProperty(value = "快递信息id")
    @TableField(value = "express_info_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long expressInfoId;

    /**
    * 更新时间
    */
    @ExcelProperty("更新时间")
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ExcelProperty("创建时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 大头笔
    */
    @ExcelProperty("大头笔")
    @ApiModelProperty(value = "大头笔")
    @TableField(value = "mark")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String mark;

    /**
    * 集包地
    */
    @ExcelProperty("集包地")
    @ApiModelProperty(value = "集包地")
    @TableField(value = "bag_addr")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String bagAddr;

    /**
    * 快递单号
    */
    @ExcelProperty("业务跟踪单号")
    @ApiModelProperty(value = "业务跟踪单号")
    @TableField(value = "tracking_no")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String trackingNo;
}