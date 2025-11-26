package work.soho.approvalprocess.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.approvalprocess.ApprovalprocessApplication;
import work.soho.approvalprocess.domain.ApprovalProcessOrderNode;
import work.soho.approvalprocess.domain.enums.ApprovalProcessOrderNodeApplyStatusEnum;
import work.soho.approvalprocess.service.ApprovalProcessOrderNodeService;
import work.soho.approvalprocess.service.ApprovalProcessOrderService;
import work.soho.approvalprocess.vo.ApprovalProcessOrderVo;
import work.soho.common.core.util.IDGeneratorUtils;

import java.util.LinkedList;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = ApprovalprocessApplication.class)
class ApprovalProcessOrderServiceImplTest {
    @Autowired
    private ApprovalProcessOrderService approvalProcessOrderServer;
    @Autowired
    private ApprovalProcessOrderNodeService approvalProcessOrderNodeService;

    @Test
    void create() {
        LinkedList<ApprovalProcessOrderVo.ContentItem> list = new LinkedList<>();
        list.add(new ApprovalProcessOrderVo.ContentItem().setKey("CONTENT").setTitle("内容").setContent("世界这么大，我想去看看！"));
        list.add(new ApprovalProcessOrderVo.ContentItem().setKey("START_TIME").setTitle("开始时间").setContent("2022-06-03 00:00:00"));
        list.add(new ApprovalProcessOrderVo.ContentItem().setKey("END_TIME").setTitle("结束时间").setContent("2023-06-03 00:00:00"));
        ApprovalProcessOrderVo approvalProcessOrderVo = new ApprovalProcessOrderVo();
        approvalProcessOrderVo.setApprovalProcessNo("1");
        approvalProcessOrderVo.setOutNo(String.valueOf(IDGeneratorUtils.snowflake().longValue()));
        approvalProcessOrderVo.setApplyUserId(1L);
        approvalProcessOrderVo.setContentItemList(list);
        approvalProcessOrderServer.create(approvalProcessOrderVo);
        System.out.println("hello wold");
    }

    @Test
    void approve() {
        ApprovalProcessOrderNode approvalProcessOrderNode = approvalProcessOrderNodeService.getById(8);
        //重置状态
        approvalProcessOrderNode.setStatus(ApprovalProcessOrderNodeApplyStatusEnum.PENDING.getStatus());
        approvalProcessOrderNodeService.updateById(approvalProcessOrderNode);

        approvalProcessOrderServer.approve(2l, approvalProcessOrderNode, ApprovalProcessOrderNodeApplyStatusEnum.AGREE, null);
    }
}
