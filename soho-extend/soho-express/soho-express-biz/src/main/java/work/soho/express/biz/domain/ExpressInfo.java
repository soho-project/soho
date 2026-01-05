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
@TableName(value ="express_info")
public class ExpressInfo implements Serializable {
    /**
    * id
    */
    @ExcelProperty("id")
    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
    * 快递名
    */
    @ExcelProperty("快递名")
    @ApiModelProperty(value = "快递名")
    @TableField(value = "name")
    private String name;

    /**
    * 快递类型
    */
    @ExcelProperty("快递类型")
    @ApiModelProperty(value = "快递类型")
    @TableField(value = "express_type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer expressType;

    /**
    * 状态
    */
    @ExcelProperty("状态")
    @ApiModelProperty(value = "状态")
    @TableField(value = "status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer status;

    /**
    * AppKey
    */
    @ExcelProperty("AppKey")
    @ApiModelProperty(value = "AppKey")
    @TableField(value = "app_key")
    private String appKey;

    /**
    * AppSecret
    */
    @ExcelProperty("AppSecret")
    @ApiModelProperty(value = "AppSecret")
    @TableField(value = "app_secret")
    private String appSecret;

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

    /**
    * 发件人名称
    */
    @ExcelProperty("发件人名称")
    @ApiModelProperty(value = "发件人名称")
    @TableField(value = "sender_name")
    private String senderName;

    /**
    * 发件人座机
    */
    @ExcelProperty("发件人座机")
    @ApiModelProperty(value = "发件人座机")
    @TableField(value = "sender_phone")
    private String senderPhone;

    /**
    * 发件人手机
    */
    @ExcelProperty("发件人手机")
    @ApiModelProperty(value = "发件人手机")
    @TableField(value = "sender_mobile")
    private String senderMobile;

    /**
    * 发件人省份
    */
    @ExcelProperty("发件人省份")
    @ApiModelProperty(value = "发件人省份")
    @TableField(value = "sender_province")
    private String senderProvince;

    /**
    * 发件人城市
    */
    @ExcelProperty("发件人城市")
    @ApiModelProperty(value = "发件人城市")
    @TableField(value = "sender_city")
    private String senderCity;

    /**
    * 发件人县/区
    */
    @ExcelProperty("发件人县/区")
    @ApiModelProperty(value = "发件人县/区")
    @TableField(value = "sender_district")
    private String senderDistrict;

    /**
    * 发件人地址
    */
    @ExcelProperty("发件人地址")
    @ApiModelProperty(value = "发件人地址")
    @TableField(value = "sender_address")
    private String senderAddress;

    /**
    * 面单账号
    */
    @ExcelProperty("面单账号")
    @ApiModelProperty(value = "面单账号")
    @TableField(value = "bill_account")
    private String billAccount;

    /**
    * 面单密码
    */
    @ExcelProperty("面单密码")
    @ApiModelProperty(value = "面单密码")
    @TableField(value = "bill_account_password")
    private String billAccountPassword;

}