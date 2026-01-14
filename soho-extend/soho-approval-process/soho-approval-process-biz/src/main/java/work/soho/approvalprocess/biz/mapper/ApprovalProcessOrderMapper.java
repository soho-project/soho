package work.soho.approvalprocess.biz.mapper;

import org.apache.ibatis.annotations.Mapper;
import work.soho.approvalprocess.biz.domain.ApprovalProcessOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import work.soho.approvalprocess.api.dto.ApprovalProcessOrderMyListDto;

import java.util.List;

/**
* @author i
* @description 针对表【approval_process_order(审批单)】的数据库操作Mapper
* @createDate 2022-05-08 01:10:25
* @Entity work.soho.approvalprocess.domain.ApprovalProcessOrder
*/
@Mapper
public interface ApprovalProcessOrderMapper extends BaseMapper<ApprovalProcessOrder> {
    List<ApprovalProcessOrder> myList(ApprovalProcessOrderMyListDto approvalProcessOrderMyListDto);
}




