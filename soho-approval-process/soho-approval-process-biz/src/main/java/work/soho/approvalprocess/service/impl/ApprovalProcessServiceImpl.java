package work.soho.approvalprocess.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import work.soho.approvalprocess.domain.ApprovalProcess;
import work.soho.approvalprocess.domain.ApprovalProcessNode;
import work.soho.approvalprocess.service.ApprovalProcessNodeService;
import work.soho.approvalprocess.service.ApprovalProcessService;
import work.soho.approvalprocess.mapper.ApprovalProcessMapper;
import org.springframework.stereotype.Service;
import work.soho.approvalprocess.vo.ApprovalProcessMetadataVo;
import work.soho.approvalprocess.vo.ApprovalProcessVo;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.core.util.JacksonUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author i
* @description 针对表【approval_process(审批流)】的数据库操作Service实现
* @createDate 2022-05-08 01:10:25
*/
@Service
@RequiredArgsConstructor
public class ApprovalProcessServiceImpl extends ServiceImpl<ApprovalProcessMapper, ApprovalProcess>
    implements ApprovalProcessService{

    private final ApprovalProcessNodeService approvalProcessNodeService;

    @Override
    public ApprovalProcess saveApprovalProcess(ApprovalProcessVo approvalProcessVo) {
        ApprovalProcess approvalProcess = BeanUtils.copy(approvalProcessVo, ApprovalProcess.class);

        approvalProcess.setCreatedTime(LocalDateTime.now());
        if(approvalProcess.getId() != null) {
            updateById(approvalProcess);
        } else {
            save(approvalProcess);
        }
        //TODO 简单粗暴 将原来的节点信息删除
        LambdaQueryWrapper<ApprovalProcessNode> delLqw = new LambdaQueryWrapper<>();
        delLqw.eq(ApprovalProcessNode::getApprovalProcessId, approvalProcess.getId());
        approvalProcessNodeService.remove(delLqw);

        //TODO 审批流完整性检查
        for (int i = 0; i < approvalProcessVo.getNodes().size(); i++) {
            ApprovalProcessVo.NodeVo item = approvalProcessVo.getNodes().get(i);
            ApprovalProcessNode approvalProcessNode = new ApprovalProcessNode();
            approvalProcessNode.setSourceUserId(item.getSourceUserId());
            approvalProcessNode.setUserId(item.getUserId());
            approvalProcessNode.setSerialNumber(i);
            approvalProcessNode.setCreatedTime(LocalDateTime.now());
            approvalProcessNode.setApprovalProcessId(approvalProcess.getId());
            approvalProcessNodeService.save(approvalProcessNode);
        }

        return approvalProcess;
    }

    @Override
    public ApprovalProcess getByNo(String no) {
        return getOne(new LambdaQueryWrapper<ApprovalProcess>().eq(ApprovalProcess::getNo, no));
    }

    @Override
    public Map<String, ApprovalProcessMetadataVo> getMetadatas(String metadata) {
        return JSONUtil.toList(metadata, ApprovalProcessMetadataVo.class).stream().collect(Collectors.toMap(ApprovalProcessMetadataVo::getKey, item->item));
    }
}




