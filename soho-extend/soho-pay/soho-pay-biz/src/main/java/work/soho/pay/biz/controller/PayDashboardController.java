package work.soho.pay.biz.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;
import work.soho.pay.biz.service.PayDashboardService;
import work.soho.pay.biz.vo.PayDashboardIndexVo;

/**
 * 支付看板控制器。
 */
@Api(tags = "支付模块 Dashboard")
@RestController
@RequiredArgsConstructor
@RequestMapping("/pay/admin/dashboard")
public class PayDashboardController {
    private final PayDashboardService payDashboardService;

    /**
     * 获取支付模块 Dashboard 首页数据。
     *
     * @return 支付看板首页数据
     */
    @GetMapping("/index")
    @Node(value = "payDashboard::index", name = "支付模块 Dashboard 首页")
    @ApiOperation(value = "获取支付模块 Dashboard 首页数据", notes = "返回支付概览、当日总支付金额曲线和按支付方式分时金额曲线")
    public R<PayDashboardIndexVo> index() {
        return R.success(payDashboardService.index());
    }
}
