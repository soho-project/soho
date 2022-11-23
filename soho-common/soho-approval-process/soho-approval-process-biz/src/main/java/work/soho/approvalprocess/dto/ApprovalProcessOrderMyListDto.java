package work.soho.approvalprocess.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ApprovalProcessOrderMyListDto {
    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("申请用户ID")
    private Long applyUserId;

    @ApiModelProperty("审批节点状态")
    private List<Integer> nodeListStatus;
}
