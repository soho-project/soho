package work.soho.pay.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 支付表
 * @TableName pay_info
 */
@TableName(value ="pay_info")
@Data
public class PayInfo implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 支付标题
     */
    @ApiModelProperty(value = "支付标题")
    @TableField(value = "title")
    private String title;

    /**
     * 支付名
     */
    @ApiModelProperty(value = "支付名")
    @TableField(value = "name")
    private String name;

    /**
     * 账号AppId
     */
    @ApiModelProperty(value = "账号AppId")
    @TableField(value = "account_app_id")
    private String accountAppId;

    /**
     * 支付ID
     */
    @ApiModelProperty(value = "支付ID")
    @TableField(value = "account_id")
    private String accountId;

    /**
     * 支付私钥
     */
    @ApiModelProperty(value = "支付私钥")
    @TableField(value = "account_private_key")
    private String accountPrivateKey;

    /**
     * 商户证书编号
     */
    @ApiModelProperty(value = "商户证书编号")
    @TableField(value = "account_serial_number")
    private String accountSerialNumber;

    /**
     * 支付公钥
     */
    @ApiModelProperty(value = "支付公钥")
    @TableField(value = "account_public_key")
    private String accountPublicKey;

    /**
     * 支付封面图片
     */
    @ApiModelProperty(value = "支付封面图片")
    @TableField(value = "account_img")
    private String accountImg;

    /**
     * 支持客户端类型;1: web,2: wap,3:app,4:微信公众号,5:微信小程序;frontType:select
     */
    @ApiModelProperty(value = "支持客户端类型;1: web,2: wap,3:app,4:微信公众号,5:微信小程序;frontType:select")
    @TableField(value = "client_type")
    private String clientType;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * 平台识别名
     */
    @ApiModelProperty(value = "平台识别名")
    @TableField(value = "platform")
    private String platform;

    /**
     * 状态; 0:禁用,1:启用;frontType:select
     */
    @ApiModelProperty(value = "状态; 0:禁用,1:启用;frontType:select")
    @TableField(value = "status")
    private Integer status;

    /**
     * 支付驱动;wechat_jsapi,wechat_h5,wechat_app,wechat_native,alipay_wap,alipay_web;frontType:select
     */
    @ApiModelProperty(value = "支付驱动;wechat_jsapi,wechat_h5,wechat_app,wechat_native,alipay_wap,alipay_web;frontType:select")
    @TableField(value = "adapter_name")
    private String adapterName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}