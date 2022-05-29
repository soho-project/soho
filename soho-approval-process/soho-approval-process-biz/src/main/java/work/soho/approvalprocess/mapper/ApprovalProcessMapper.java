package work.soho.approvalprocess.mapper;

import org.apache.ibatis.annotations.Mapper;
import work.soho.approvalprocess.domain.ApprovalProcess;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author i
* @description 针对表【approval_process(审批流)】的数据库操作Mapper
* @createDate 2022-05-08 01:10:25
* @Entity work.soho.approvalprocess.domain.ApprovalProcess
*/
@Mapper
public interface ApprovalProcessMapper extends BaseMapper<ApprovalProcess> {

}




