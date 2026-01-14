package work.soho.approvalprocess.api.service;

import work.soho.approvalprocess.api.dto.ApprovalProcessOrderDto;
import work.soho.approvalprocess.api.vo.ApprovalProcessOrderVo;

public interface ApprovalProcessOrderApiService {
    /**
     * 创建审批单
     *
     * @param approvalProcessOrderVo
     * @return
     */
    ApprovalProcessOrderDto push(ApprovalProcessOrderVo approvalProcessOrderVo);

}
