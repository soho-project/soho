package work.soho.approvalprocess.service;

import work.soho.approvalprocess.domain.ApprovalProcessOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.approvalprocess.domain.ApprovalProcessOrderNode;
import work.soho.approvalprocess.domain.enums.ApprovalProcessOrderNodeApplyStatusEnum;
import work.soho.approvalprocess.dto.ApprovalProcessOrderMyListDto;
import work.soho.approvalprocess.vo.ApprovalProcessOrderVo;

import java.util.List;

/**
* @author i
* @description 针对表【approval_process_order(审批单)】的数据库操作Service
* @createDate 2022-05-08 01:10:25
*/
public interface ApprovalProcessOrderService extends IService<ApprovalProcessOrder> {
    /**
     * 创建审批单
     *
     * @param approvalProcessOrderVo
     * @return
     */
    ApprovalProcessOrder create(ApprovalProcessOrderVo approvalProcessOrderVo);

    /**
     * 执行审批动作
     *
     * @param userId
     * @param approvalProcessOrderNode
     * @param applyStatus
     */
    void approve(Long userId, ApprovalProcessOrderNode approvalProcessOrderNode, ApprovalProcessOrderNodeApplyStatusEnum applyStatus);

    /**
     * 获取当前审批节点
     *
     * @param approvalProcessOrder
     * @return
     */
    List<ApprovalProcessOrderNode> getActiveOrderNodeList(ApprovalProcessOrder approvalProcessOrder);

    /**
     * 检测获取审批单真实状态
     *
     * 通过节点计算状态
     *
     * @param approvalProcessOrder
     * @return
     */
    ApprovalProcessOrderNodeApplyStatusEnum getApprovalProcessStatus(ApprovalProcessOrder approvalProcessOrder);

    List<ApprovalProcessOrder> myList(ApprovalProcessOrderMyListDto approvalProcessOrderMyListDto);
}
