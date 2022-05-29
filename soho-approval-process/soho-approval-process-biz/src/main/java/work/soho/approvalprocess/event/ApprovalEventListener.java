package work.soho.approvalprocess.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class ApprovalEventListener implements ApplicationListener<ApprovalEvent> {
    @Override
    @Async
    public void onApplicationEvent(ApprovalEvent event) {
        // TODO document why this method is empty
        System.out.println("=============================");
        System.out.println(event);
    }
}
