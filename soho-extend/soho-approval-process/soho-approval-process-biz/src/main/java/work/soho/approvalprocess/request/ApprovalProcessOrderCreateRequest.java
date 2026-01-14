package work.soho.approvalprocess.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ApprovalProcessOrderCreateRequest {
    @ApiModelProperty("审批流ID")
    private Integer approvalProcessId;

    @ApiModelProperty("审批单编号；可选")
    private String outNo;

    @ApiModelProperty("审批内容")
    private Map<String, String> content;
}
