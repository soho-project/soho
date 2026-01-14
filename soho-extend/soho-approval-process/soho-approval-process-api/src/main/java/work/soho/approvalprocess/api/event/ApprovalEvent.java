package work.soho.approvalprocess.api.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 审批事件
 */
@Setter
@Getter
public class ApprovalEvent extends ApplicationEvent {


    /**
     * 审批流编号
     */
    private String approvalProcessNo;

    public ApprovalEvent(Object source) {
        super(source);
    }
}
