package work.soho.approvalprocess.event;

import org.springframework.context.ApplicationEvent;

/**
 * 审批事件
 */
public class ApprovalEvent extends ApplicationEvent {
    public ApprovalEvent(Object source) {
        super(source);
    }
}