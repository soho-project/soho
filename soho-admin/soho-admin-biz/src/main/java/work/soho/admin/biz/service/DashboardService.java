package work.soho.admin.biz.service;

import work.soho.admin.api.vo.DashboardIndexVo;

/**
 * Dashboard 聚合服务。
 */
public interface DashboardService {
    /**
     * 获取 Dashboard 首页数据。
     *
     * @return Dashboard 聚合结果
     */
    DashboardIndexVo index();
}
