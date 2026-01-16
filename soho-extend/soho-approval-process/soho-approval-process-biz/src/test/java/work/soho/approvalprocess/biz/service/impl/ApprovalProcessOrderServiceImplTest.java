package work.soho.approvalprocess.biz.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.approvalprocess.api.vo.ApprovalProcessOrderVo;
import work.soho.approvalprocess.biz.domain.ApprovalProcessOrderNode;
import work.soho.approvalprocess.biz.domain.enums.ApprovalProcessOrderNodeApplyStatusEnum;
import work.soho.approvalprocess.biz.service.ApprovalProcessOrderNodeService;
import work.soho.approvalprocess.biz.service.ApprovalProcessOrderService;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.test.TestApp;

import java.util.LinkedList;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("dev")
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

        approvalProcessOrderServer.approve(2L, approvalProcessOrderNode, ApprovalProcessOrderNodeApplyStatusEnum.AGREE, null);
    }
}
