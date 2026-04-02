package work.soho.pay.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 自定义二维码支付上报记录。
 */
@TableName(value = "pay_manual_report")
@Data
public class PayManualReport implements Serializable {
    /**
     * 主键 ID。
     */
    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 支付方式 ID（pay_info.id）。
     */
    @ApiModelProperty(value = "支付方式ID")
    @TableField(value = "pay_id")
    private Integer payId;

    /**
     * 支付单 ID（pay_order.id）。
     */
    @ApiModelProperty(value = "支付单ID")
    @TableField(value = "pay_order_id")
    private Integer payOrderId;

    /**
     * 支付单号（pay_order.order_no）。
     */
    @ApiModelProperty(value = "支付单号")
    @TableField(value = "order_no")
    private String orderNo;

    /**
     * 付款人姓名。
     */
    @ApiModelProperty(value = "付款人姓名")
    @TableField(value = "payer_name")
    private String payerName;

    /**
     * 用户上报支付金额。
     */
    @ApiModelProperty(value = "上报支付金额")
    @TableField(value = "report_amount")
    private BigDecimal reportAmount;

    /**
     * 用户上报支付时间。
     */
    @ApiModelProperty(value = "上报支付时间")
    @TableField(value = "report_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportTime;

    /**
     * 支付供应商单号。
     */
    @ApiModelProperty(value = "支付供应商单号")
    @TableField(value = "supplier_trade_no")
    private String supplierTradeNo;

    /**
     * 用户备注。
     */
    @ApiModelProperty(value = "用户备注")
    @TableField(value = "report_remark")
    private String reportRemark;

    /**
     * 匹配状态。
     */
    @ApiModelProperty(value = "匹配状态")
    @TableField(value = "match_status")
    private Integer matchStatus;

    /**
     * 匹配得分。
     */
    @ApiModelProperty(value = "匹配得分")
    @TableField(value = "match_score")
    private Integer matchScore;

    /**
     * 匹配说明或审核备注。
     */
    @ApiModelProperty(value = "匹配说明")
    @TableField(value = "match_note")
    private String matchNote;

    /**
     * 审核人。
     */
    @ApiModelProperty(value = "审核人")
    @TableField(value = "reviewed_by")
    private String reviewedBy;

    /**
     * 审核时间。
     */
    @ApiModelProperty(value = "审核时间")
    @TableField(value = "reviewed_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedTime;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 更新时间。
     */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
