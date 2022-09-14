package work.soho.admin.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import work.soho.admin.domain.AdminContent;
import work.soho.admin.service.AdminContentService;
import work.soho.api.admin.event.DashboardEvent;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@Component
public class ListCardEventListener {
    @Autowired
    private AdminContentService adminContentService;

    @EventListener
    public void listCard(DashboardEvent event) {
        LambdaQueryWrapper<AdminContent> lambdaQueryWrapper = new LambdaQueryWrapper<AdminContent>();
        lambdaQueryWrapper.orderByDesc(AdminContent::getId);
        lambdaQueryWrapper.last(" limit 6");
        List<AdminContent> list = adminContentService.list(lambdaQueryWrapper);
        LinkedList<HashMap<String, Object>> data = new LinkedList<>();
        for (AdminContent item: list) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<>();
            row.put("ID", item.getId());
            row.put("标题", item.getTitle());
            row.put("创建时间", item.getCreatedTime());
            data.add(row);
        }
        event.getListCards().add(data);
    }
}
