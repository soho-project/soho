package work.soho.approvalprocess.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import work.soho.approvalprocess.domain.ApprovalProcess;
import work.soho.approvalprocess.domain.ApprovalProcessNode;
import work.soho.approvalprocess.domain.ApprovalProcessOrder;
import work.soho.approvalprocess.domain.ApprovalProcessOrderNode;
import work.soho.approvalprocess.dto.ApprovalProcessOrderMyListDto;
import work.soho.approvalprocess.event.ApprovalEvent;
import work.soho.approvalprocess.service.ApprovalProcessNodeService;
import work.soho.approvalprocess.service.ApprovalProcessOrderNodeService;
import work.soho.approvalprocess.service.ApprovalProcessOrderService;
import work.soho.approvalprocess.mapper.ApprovalProcessOrderMapper;
import org.springframework.stereotype.Service;
import work.soho.approvalprocess.service.ApprovalProcessService;
import work.soho.approvalprocess.vo.ApprovalProcessOrderVo;
import work.soho.common.core.support.SpringContextHolder;
import work.soho.common.core.util.BeanUtils;

import work.soho.approvalprocess.domain.enums.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
* @author i
* @description 针对表【approval_process_order(审批单)】的数据库操作Service实现
* @createDate 2022-05-08 01:10:25
*/
@Service
@RequiredArgsConstructor
public class ApprovalProcessOrderServiceImpl extends ServiceImpl<ApprovalProcessOrderMapper, ApprovalProcessOrder>
    implements ApprovalProcessOrderService{

    private final ApprovalProcessService approvalProcessService;
    private final ApprovalProcessNodeService approvalProcessNodeService;
    private final ApprovalProcessOrderNodeService approvalProcessOrderNodeService;

    @Override
    public ApprovalProcessOrder create(ApprovalProcessOrderVo approvalProcessOrderVo) {
        //查询审批流信息
        ApprovalProcess approvalProcess = approvalProcessService.getByNo(approvalProcessOrderVo.getApprovalProcessNo());
        Assert.notNull(approvalProcess, "没有找到对应的审批流信息； 请联系管理员配置");
        //检查对应订单是否已经创建过审批订单
        LambdaQueryWrapper<ApprovalProcessOrder> orderLqw = new LambdaQueryWrapper<>();
        orderLqw.eq(ApprovalProcessOrder::getApprovalProcessId, approvalProcess.getId());
        orderLqw.eq(ApprovalProcessOrder::getOutNo, approvalProcessOrderVo.getOutNo());
        ApprovalProcessOrder order = getOne(orderLqw);
        System.out.println(order);
        Assert.isNull(order, "该审批单已经存在， 请勿重复提交");

        ApprovalProcessOrder approvalProcessOrder = BeanUtils.copy(approvalProcessOrderVo, ApprovalProcessOrder.class);
        approvalProcessOrder.setCreatedTime(LocalDateTime.now());
        approvalProcessOrder.setContent(JSONUtil.toJsonStr(approvalProcessOrderVo.getContentItemList()));
        approvalProcessOrder.setStatus(ApprovalProcessOrderStatusEnum.PENDING.getStatus());
        approvalProcessOrder.setApplyStatus(ApprovalProcessOrderApplyStatusEnum.PENDING.getStatus());
        approvalProcessOrder.setApprovalProcessId(approvalProcess.getId());
        approvalProcessOrder.setCreatedTime(LocalDateTime.now());
        approvalProcessOrder.setApplyUserId(approvalProcessOrderVo.getApplyUserId());
        save(approvalProcessOrder);
        //复制审批流信息
        List<ApprovalProcessNode> nodes = approvalProcessNodeService.list(new LambdaQueryWrapper<ApprovalProcessNode>()
                .eq(ApprovalProcessNode::getApprovalProcessId, approvalProcess.getId()));
        nodes.forEach(item->{
            ApprovalProcessOrderNode approvalProcessOrderNode = new ApprovalProcessOrderNode();
            approvalProcessOrderNode.setOrderId(approvalProcessOrder.getId());
            approvalProcessOrderNode.setCreatedTime(LocalDateTime.now());
            approvalProcessOrderNode.setStatus(ApprovalProcessOrderNodeApplyStatusEnum.WAITING.getStatus());
            approvalProcessOrderNode.setUserId(item.getUserId());
            approvalProcessOrderNode.setReply(null);
            approvalProcessOrderNode.setSourceUserId(item.getSourceUserId());
            approvalProcessOrderNode.setSerialNumber(item.getSerialNumber());
            //确定节点状态
            if(item.getSourceUserId() == 0) {
                approvalProcessOrderNode.setStatus(ApprovalProcessOrderNodeApplyStatusEnum.PENDING.getStatus());
            }
            approvalProcessOrderNodeService.save(approvalProcessOrderNode);
        });

        return approvalProcessOrder;
    }

    @Override
    public void approve(Long userId, ApprovalProcessOrderNode approvalProcessOrderNode, ApprovalProcessOrderNodeApplyStatusEnum applyStatus) {
        ApprovalProcessOrder approvalProcessOrder = getById(approvalProcessOrderNode.getOrderId());
        Assert.notNull(approvalProcessOrder, "没有找到审批单");
        List<ApprovalProcessOrderNode> nodes = getActiveOrderNodeList(approvalProcessOrder);
        nodes.forEach(item->{
            if(item.getUserId().equals(userId)) {
                if(applyStatus.equals(ApprovalProcessOrderNodeApplyStatusEnum.REJECT)) {
                    //驳回审批
                    approvalProcessOrderNode.setStatus(ApprovalProcessOrderNodeApplyStatusEnum.REJECT.getStatus());
                } else if(applyStatus.equals(ApprovalProcessOrderNodeApplyStatusEnum.AGREE)) {
                    //同意审批
                    approvalProcessOrderNode.setStatus(ApprovalProcessOrderNodeApplyStatusEnum.AGREE.getStatus());
                } else if(applyStatus.equals(ApprovalProcessOrderNodeApplyStatusEnum.PASS_ON)) {
                    //TODO 转交审批
                }
                approvalProcessOrderNode.setApprovalTime(LocalDateTime.now());
                System.out.println(approvalProcessOrderNode);
                approvalProcessOrderNodeService.updateById(approvalProcessOrderNode);
                //获取下一节点， 修改下一节点状态为， 待处理状态
                LambdaQueryWrapper<ApprovalProcessOrderNode> lqw = new LambdaQueryWrapper<>();
                lqw.eq(ApprovalProcessOrderNode::getOrderId, approvalProcessOrder.getId());
                lqw.eq(ApprovalProcessOrderNode::getSourceUserId, approvalProcessOrderNode.getUserId());
                lqw.eq(ApprovalProcessOrderNode::getStatus, ApprovalProcessOrderNodeApplyStatusEnum.WAITING.getStatus());
                lqw.last(" limit 1");
                ApprovalProcessOrderNode nextNode = approvalProcessOrderNodeService.getOne(lqw);
                //TODO 处理转交
                //修改下一节点条件： 1 存在下一节点 2 当前审批状态不为驳回
                if(nextNode != null && approvalProcessOrderNode.getStatus() != ApprovalProcessOrderNodeApplyStatusEnum.REJECT.getStatus()) {
                    nextNode.setStatus(ApprovalProcessOrderNodeApplyStatusEnum.PENDING.getStatus());
                    approvalProcessOrderNodeService.updateById(nextNode);
                }

                //判断当前审批单状态
                ApprovalProcessOrderNodeApplyStatusEnum nodeApplyStatusEnum = getApprovalProcessStatus(approvalProcessOrder);

                if(ApprovalProcessOrderNodeApplyStatusEnum.REJECT.getStatus() == approvalProcessOrderNode.getStatus()) {
                    //当前审批为驳回 则直接结束审批
                    approvalProcessOrder.setApplyStatus(ApprovalProcessOrderApplyStatusEnum.REJECT.getStatus());
                    approvalProcessOrder.setStatus(ApprovalProcessOrderStatusEnum.FINISH.getStatus());
                } else if(nodeApplyStatusEnum.equals(ApprovalProcessOrderNodeApplyStatusEnum.AGREE)) {
                    approvalProcessOrder.setApplyStatus(ApprovalProcessOrderApplyStatusEnum.AGREE.getStatus());
                    approvalProcessOrder.setStatus(ApprovalProcessOrderStatusEnum.FINISH.getStatus());
                } else if(nodeApplyStatusEnum.equals(ApprovalProcessOrderNodeApplyStatusEnum.REJECT)) {
                    approvalProcessOrder.setApplyStatus(ApprovalProcessOrderApplyStatusEnum.REJECT.getStatus());
                    approvalProcessOrder.setStatus(ApprovalProcessOrderStatusEnum.FINISH.getStatus());
                } else {
                    //TOOD 转交处理
                }
                updateById(approvalProcessOrder);
                SpringContextHolder.getApplicationContext().publishEvent(new ApprovalEvent(approvalProcessOrder));
            }
        });
    }

    /**
     * 获取当前活跃的审批节点
     *
     * TODO 并发审核支持
     *
     * @param approvalProcessOrder
     * @return
     */
    public List<ApprovalProcessOrderNode> getActiveOrderNodeList(ApprovalProcessOrder approvalProcessOrder) {
        LambdaQueryWrapper<ApprovalProcessOrderNode> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ApprovalProcessOrderNode::getOrderId, approvalProcessOrder.getId());
        lambdaQueryWrapper.eq(ApprovalProcessOrderNode::getStatus, ApprovalProcessOrderNodeApplyStatusEnum.PENDING.getStatus());
        lambdaQueryWrapper.orderByAsc(ApprovalProcessOrderNode::getSerialNumber);
        lambdaQueryWrapper.last(" limit 1");
        return approvalProcessOrderNodeService.list(lambdaQueryWrapper);
    }

    /**
     * 获取审批单当前状态
     *
     * @return
     */
    public ApprovalProcessOrderNodeApplyStatusEnum getApprovalProcessStatus(ApprovalProcessOrder approvalProcessOrder) {
        List<ApprovalProcessOrderNode> nodes = approvalProcessOrderNodeService.list(new LambdaQueryWrapper<ApprovalProcessOrderNode>()
                .eq(ApprovalProcessOrderNode::getOrderId, approvalProcessOrder.getId())
        );

        HashMap<Long, ApprovalProcessOrderNode> nodeHashMap = new HashMap<>();
        HashMap<Long, ApprovalProcessOrderNode> parentNodeHashMap = new HashMap<>();
        AtomicReference<ApprovalProcessOrderNode> lastNode = new AtomicReference<>();
        nodes.forEach(item->{
            nodeHashMap.put(item.getUserId(), item);
            parentNodeHashMap.put(item.getSourceUserId(), item);
            if(item.getSourceUserId() == 0) {
                lastNode.set(item);
            }
        });

        //查找最后节点
        while (parentNodeHashMap.get(lastNode.get().getUserId())!= null) {
            //检查是否重复绕圈 死循环
            if(nodeHashMap.get(nodeHashMap.get(lastNode.get().getUserId()).getId()) != null) {
                throw new RuntimeException("数据异常，死循环");
            }
            lastNode.set(parentNodeHashMap.get(lastNode.get().getUserId()));
        }

        return ApprovalProcessOrderNodeApplyStatusEnum.getByStatus(lastNode.get().getStatus());
    }

    /**
     * 获取审批单信息
     *
     * @param approvalProcessOrderMyListDto
     * @return
     */
    public List<ApprovalProcessOrder> myList(ApprovalProcessOrderMyListDto approvalProcessOrderMyListDto) {
        return getBaseMapper().myList(approvalProcessOrderMyListDto);
    }
}




