package work.soho.approvalprocess.mapper;

import org.apache.ibatis.annotations.Mapper;
import work.soho.approvalprocess.domain.ApprovalProcessOrderNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author i
* @description 针对表【approval_process_order_node(审批单节点)】的数据库操作Mapper
* @createDate 2022-05-08 01:10:25
* @Entity work.soho.approvalprocess.domain.ApprovalProcessOrderNode
*/
@Mapper
public interface ApprovalProcessOrderNodeMapper extends BaseMapper<ApprovalProcessOrderNode> {

}




