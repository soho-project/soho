package work.soho.pay.biz.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 管理端人工审核上报请求。
 */
@Data
public class PayManualReportAuditRequest {
    /**
     * 上报记录 ID。
     */
    @ApiModelProperty(value = "上报记录ID", required = true)
    private Long reportId;

    /**
     * 是否审核通过。
     */
    @ApiModelProperty(value = "是否通过", required = true)
    private Boolean approved;

    /**
     * 审核备注。
     */
    @ApiModelProperty(value = "审核备注")
    private String note;

    /**
     * 审核通过时的目标支付单号（可选，默认使用上报里的 orderNo）。
     */
    @ApiModelProperty(value = "目标支付单号")
    private String targetOrderNo;

    /**
     * 审核人。
     */
    @ApiModelProperty(value = "审核人")
    private String reviewer;
}
