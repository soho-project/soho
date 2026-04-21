package work.soho.admin.api.dashboard;

import work.soho.admin.api.vo.DashboardBuildContext;

import java.util.List;

/**
 * Dashboard 列表卡片提供者。
 */
public interface DashboardListCardProvider {
    /**
     * 提供列表卡片。
     *
     * @param context Dashboard 构建上下文
     * @return 列表卡片列表
     */
    List<List> provide(DashboardBuildContext context);
}
