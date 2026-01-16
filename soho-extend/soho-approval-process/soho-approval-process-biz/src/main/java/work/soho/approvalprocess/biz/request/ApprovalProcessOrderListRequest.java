package work.soho.approvalprocess.biz.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

@Data
public class ApprovalProcessOrderListRequest {
    @ApiModelProperty("请求类型；null 所有我的相关的审批单； 1 我申请的审批单  2 我审批过的审批单 3 我参与的待审批单")
    @Nullable
    private Integer type;

    @ApiModelProperty("审批单外部单号")
    @Nullable
    private String outNo;

    @ApiModelProperty("创建时间区间")
    @Nullable
    private List<Date> createTime;

}
