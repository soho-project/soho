package work.soho.approvalprocess.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApprovalRequest {
    @ApiModelProperty("审批单节点ID")
    private Integer id;

    @ApiModelProperty("执行审批的人")
    private Long userId;

    @ApiModelProperty("审批回复")
    private String reply;

    @ApiModelProperty("审批状态")
    private Integer status;
}
