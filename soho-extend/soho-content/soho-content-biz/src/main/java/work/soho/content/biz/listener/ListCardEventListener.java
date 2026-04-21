package work.soho.content.biz.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import work.soho.admin.api.dashboard.DashboardListCardProvider;
import work.soho.admin.api.vo.DashboardBuildContext;
import work.soho.content.biz.domain.ContentInfo;
import work.soho.content.biz.service.AdminContentService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 内容 Dashboard 列表卡片提供者。
 */
@Component
@Order(200)
@RequiredArgsConstructor
public class ListCardEventListener implements DashboardListCardProvider {
    private final AdminContentService adminContentService;

    @Override
    public List<List> provide(DashboardBuildContext context) {
        LambdaQueryWrapper<ContentInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(ContentInfo::getId);
        lambdaQueryWrapper.last(" limit 6");
        List<ContentInfo> list = adminContentService.list(lambdaQueryWrapper);
        LinkedList<HashMap<String, Object>> data = new LinkedList<>();
        for (ContentInfo item : list) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<>();
            row.put("ID", item.getId());
            row.put("标题", item.getTitle());
            row.put("创建时间", item.getCreatedTime());
            data.add(row);
        }
        return List.of(data);
    }
}
