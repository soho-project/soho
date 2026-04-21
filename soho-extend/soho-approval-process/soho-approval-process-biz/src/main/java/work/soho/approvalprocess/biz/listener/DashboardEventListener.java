package work.soho.approvalprocess.biz.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import work.soho.admin.api.dashboard.DashboardListCardProvider;
import work.soho.admin.api.vo.DashboardBuildContext;
import work.soho.approvalprocess.biz.domain.ApprovalProcess;
import work.soho.approvalprocess.biz.service.ApprovalProcessService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 审批流 Dashboard 列表卡片提供者。
 */
@Component
@Order(300)
@RequiredArgsConstructor
public class DashboardEventListener implements DashboardListCardProvider {
    private final ApprovalProcessService approvalProcessService;

    @Override
    public List<List> provide(DashboardBuildContext context) {
        LambdaQueryWrapper<ApprovalProcess> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(ApprovalProcess::getId);
        lambdaQueryWrapper.last(" limit 6");
        List<ApprovalProcess> list = approvalProcessService.list(lambdaQueryWrapper);
        LinkedList<HashMap<String, Object>> data = new LinkedList<>();
        for (ApprovalProcess item : list) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<>();
            row.put("ID", item.getId());
            row.put("编号", item.getNo());
            row.put("申请名", item.getName());
            row.put("创建时间", item.getCreatedTime());
            data.add(row);
        }
        return List.of(data);
    }
}
