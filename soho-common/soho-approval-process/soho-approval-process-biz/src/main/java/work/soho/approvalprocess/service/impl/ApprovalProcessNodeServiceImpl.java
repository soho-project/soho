package work.soho.approvalprocess.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.soho.approvalprocess.domain.ApprovalProcessNode;
import work.soho.approvalprocess.service.ApprovalProcessNodeService;
import work.soho.approvalprocess.mapper.ApprovalProcessNodeMapper;
import org.springframework.stereotype.Service;

/**
* @author i
* @description 针对表【approval_process_node(审批流节点)】的数据库操作Service实现
* @createDate 2022-05-08 01:10:25
*/
@Service
public class ApprovalProcessNodeServiceImpl extends ServiceImpl<ApprovalProcessNodeMapper, ApprovalProcessNode>
    implements ApprovalProcessNodeService{

}




