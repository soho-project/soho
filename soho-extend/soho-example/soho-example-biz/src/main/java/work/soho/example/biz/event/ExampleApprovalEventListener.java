package work.soho.example.biz.event;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import work.soho.approvalprocess.domain.ApprovalProcessOrder;
import work.soho.approvalprocess.event.ApprovalEvent;
import work.soho.example.biz.domain.Example;
import work.soho.example.biz.service.ExampleService;

@RequiredArgsConstructor
@Component
@Log4j2
public class ExampleApprovalEventListener implements ApplicationListener<ApprovalEvent> {
    /**
     * 审批流ID
     */
    private static final String APPROVAL_NO = "4";

    final private ExampleService exampleService;

    @Override
    @Async
    public void onApplicationEvent(ApprovalEvent event) {
        ApprovalProcessOrder order = (ApprovalProcessOrder) event.getSource();
        //检查师傅为当前对应审批流
        if(!event.getApprovalProcessNo().equals(APPROVAL_NO)) {
            return;
        }
        //处理业务逻辑，　修改样例状态为审批通过
        if(order.getStatus() == 1) {
            Example example = exampleService.getById(order.getOutNo());
            if(example != null) {
                if(order.getApplyStatus() == 2) {
                    //审批通过
                    example.setStatus(1);
                } else if(order.getApplyStatus() == 1){
                    //审批拒绝
                    example.setStatus(2);
                }
                exampleService.updateById(example);
            } else {
                log.error("无效审批单: {}", event);
            }
        } else {
            log.info("审批中间环节： {}", event);
        }
    }
}