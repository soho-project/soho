package work.soho.admin.api.dashboard;

import work.soho.admin.api.vo.DashboardBuildContext;
import work.soho.admin.api.vo.NumberCardVo;

import java.util.List;

/**
 * Dashboard 数字卡片提供者。
 */
public interface DashboardNumberCardProvider {
    /**
     * 提供数字卡片。
     *
     * @param context Dashboard 构建上下文
     * @return 数字卡片列表
     */
    List<NumberCardVo> provide(DashboardBuildContext context);
}
