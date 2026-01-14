package work.soho.approvalprocess.service;

import work.soho.approvalprocess.domain.ApprovalProcess;
import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.approvalprocess.vo.ApprovalProcessMetadataVo;
import work.soho.approvalprocess.vo.ApprovalProcessVo;

import java.util.Map;

/**
* @author i
* @description 针对表【approval_process(审批流)】的数据库操作Service
* @createDate 2022-05-08 01:10:25
*/
public interface ApprovalProcessService extends IService<ApprovalProcess> {
    ApprovalProcess saveApprovalProcess(ApprovalProcessVo approvalProcessVo);
    ApprovalProcess getByNo(String no);
    Map<String, ApprovalProcessMetadataVo> getMetadatas(String metadata);
}
