package work.soho.approvalprocess.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import work.soho.admin.AdminApplication;
import work.soho.approvalprocess.SohoApprovalProcessBizApplication;
import work.soho.approvalprocess.domain.ApprovalProcessOrderNode;
import work.soho.approvalprocess.domain.enums.ApprovalProcessOrderNodeApplyStatusEnum;
import work.soho.approvalprocess.vo.ApprovalProcessOrderVo;
import work.soho.common.core.util.IDGeneratorUtils;

import java.util.LinkedList;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = AdminApplication.class)
class ApprovalProcessOrderServiceImplTest {
    @Autowired
    private ApprovalProcessOrderService approvalProcessOrderServer;
    @Autowired
    private ApprovalProcessOrderNodeService approvalProcessOrderNodeService;

    @Test
    void create() {
        LinkedList<ApprovalProcessOrderVo.ContentItem> list = new LinkedList<>();
        list.add(new ApprovalProcessOrderVo.ContentItem().setKey("TITLE").setTitle("标题").setContent("这里是测试标题"));
        list.add(new ApprovalProcessOrderVo.ContentItem().setKey("CONTENT").setTitle("内容").setContent("这里是测试审批内容"));
        ApprovalProcessOrderVo approvalProcessOrderVo = new ApprovalProcessOrderVo();
        approvalProcessOrderVo.setApprovalProcessNo("true");
        approvalProcessOrderVo.setOutNo(String.valueOf(IDGeneratorUtils.snowflake().longValue()));
        approvalProcessOrderVo.setApplyUserId(1l);
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