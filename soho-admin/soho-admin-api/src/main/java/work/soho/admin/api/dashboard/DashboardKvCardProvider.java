package work.soho.admin.api.dashboard;

import work.soho.admin.api.vo.DashboardBuildContext;

import java.util.List;

/**
 * Dashboard 键值卡片提供者。
 */
public interface DashboardKvCardProvider {
    /**
     * 提供键值卡片。
     *
     * @param context Dashboard 构建上下文
     * @return 键值卡片列表
     */
    List<List> provide(DashboardBuildContext context);
}
