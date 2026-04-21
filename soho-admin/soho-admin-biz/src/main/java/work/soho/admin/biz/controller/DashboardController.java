package work.soho.admin.biz.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.api.vo.DashboardIndexVo;
import work.soho.admin.biz.service.DashboardService;
import work.soho.common.core.result.R;

/**
 * Dashboard 控制器。
 */
@RestController
@Api(tags = "Dashboard")
@RequestMapping("/admin/admin/adminDashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    /**
     * 获取 Dashboard 首页数据。
     *
     * @return Dashboard 聚合结果
     */
    @GetMapping("/index")
    public R<DashboardIndexVo> index() {
        return R.success(dashboardService.index());
    }
}
