package work.soho.pay.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 支付单
 * @TableName pay_order
 */
@TableName(value ="pay_order")
@Data
public class PayOrder implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 支付方式ID
     */
    @TableField(value = "pay_id")
    private Integer payId;

    /**
     * 支付单号
     */
    @TableField(value = "order_no")
    private String orderNo;

    /**
     * 外部跟踪单号
     */
    @TableField(value = "tracking_no")
    private String trackingNo;

    /**
     * 支付供应商跟踪ID；例如微信，支付宝支付单号
     */
    @TableField(value = "transaction_id")
    private String transactionId;

    /**
     * 支付金额
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 支付单状态;1:待支付,10:已扫码,20:支付成功,30:支付失败;frontType:select
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 支付时间;;frontType:datetime
     */
    @TableField(value = "payed_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payedTime;

    /**
     * 订单客户端回调地址
     */
    @TableField(value = "notify_url")
    private String notifyUrl;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}