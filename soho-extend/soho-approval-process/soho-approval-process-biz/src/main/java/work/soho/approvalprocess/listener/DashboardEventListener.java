package work.soho.approvalprocess.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import work.soho.admin.api.event.DashboardEvent;
import work.soho.approvalprocess.domain.ApprovalProcess;
import work.soho.approvalprocess.service.ApprovalProcessService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@Component
public class DashboardEventListener {
    @Autowired
    private ApprovalProcessService approvalProcessService;

    @EventListener
    public void listCard(DashboardEvent event) {
        LambdaQueryWrapper<ApprovalProcess> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(ApprovalProcess::getId);
        lambdaQueryWrapper.last(" limit 6");
        List<ApprovalProcess> list = approvalProcessService.list(lambdaQueryWrapper);
        LinkedList<HashMap<String, Object>> data = new LinkedList<>();
        for (ApprovalProcess item: list) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<>();
            row.put("ID", item.getId());
            row.put("编号", item.getNo());
            row.put("申请名", item.getName());
            row.put("创建时间", item.getCreatedTime());
            data.add(row);
        }
        event.getListCards().add(data);
    }
}
