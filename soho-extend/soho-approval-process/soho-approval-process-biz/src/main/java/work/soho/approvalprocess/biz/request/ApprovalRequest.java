package work.soho.approvalprocess.biz.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApprovalRequest {
    @ApiModelProperty("审批单节点ID")
    private Integer id;

    /**
     * 执行审批的人
     * 客户端无须传递
     */
    @ApiModelProperty("执行审批的人")
    private Long userId;

    @ApiModelProperty("审批回复")
    private String reply;

    @ApiModelProperty("审批状态")
    private Integer status;

    @ApiModelProperty("下一审批人；状态为转交的时候必选")
    private Long nextUserId;
}
