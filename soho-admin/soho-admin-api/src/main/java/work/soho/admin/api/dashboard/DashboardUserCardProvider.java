package work.soho.admin.api.dashboard;

import work.soho.admin.api.vo.DashboardBuildContext;
import work.soho.admin.api.vo.DashboardUserCardVo;

import java.util.List;

/**
 * Dashboard 用户卡片提供者。
 */
public interface DashboardUserCardProvider {
    /**
     * 提供用户卡片。
     *
     * @param context Dashboard 构建上下文
     * @return 用户卡片列表
     */
    List<DashboardUserCardVo> provide(DashboardBuildContext context);
}
