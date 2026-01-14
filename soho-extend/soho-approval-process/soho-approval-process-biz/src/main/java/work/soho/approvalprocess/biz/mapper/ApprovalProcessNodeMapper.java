package work.soho.approvalprocess.biz.mapper;

import org.apache.ibatis.annotations.Mapper;
import work.soho.approvalprocess.biz.domain.ApprovalProcessNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author i
* @description 针对表【approval_process_node(审批流节点)】的数据库操作Mapper
* @createDate 2022-05-08 01:10:25
* @Entity work.soho.approvalprocess.domain.ApprovalProcessNode
*/
@Mapper
public interface ApprovalProcessNodeMapper extends BaseMapper<ApprovalProcessNode> {

}




