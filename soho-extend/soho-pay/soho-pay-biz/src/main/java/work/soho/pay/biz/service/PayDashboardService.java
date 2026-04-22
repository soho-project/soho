package work.soho.pay.biz.service;

import work.soho.pay.biz.vo.PayDashboardIndexVo;

/**
 * 支付看板服务。
 */
public interface PayDashboardService {
    /**
     * 获取支付模块 Dashboard 首页数据。
     *
     * @return 支付看板首页数据
     */
    PayDashboardIndexVo index();
}
