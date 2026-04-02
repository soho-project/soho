package work.soho.pay.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.common.security.annotation.Node;
import work.soho.pay.biz.domain.PayManualReport;
import work.soho.pay.biz.request.PayManualReportAuditRequest;
import work.soho.pay.biz.service.PayManualReportService;

import java.util.List;
import java.util.Map;

/**
 * 自定义二维码支付上报管理接口。
 */
@Api(tags = "自定义二维码支付上报管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("/pay/admin/payManualReport")
public class PayManualReportController {
    private final PayManualReportService payManualReportService;

    /**
     * 查询上报审核列表。
     *
     * @param payManualReport 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    @Node(value = "payManualReport::list", name = "支付上报审核列表")
    public R<PageSerializable<PayManualReport>> list(PayManualReport payManualReport) {
        PageUtils.startPage();
        LambdaQueryWrapper<PayManualReport> lqw = new LambdaQueryWrapper<>();
        lqw.eq(payManualReport.getId() != null, PayManualReport::getId, payManualReport.getId());
        lqw.eq(payManualReport.getPayId() != null, PayManualReport::getPayId, payManualReport.getPayId());
        lqw.eq(payManualReport.getMatchStatus() != null, PayManualReport::getMatchStatus, payManualReport.getMatchStatus());
        lqw.like(StringUtils.isNotBlank(payManualReport.getOrderNo()), PayManualReport::getOrderNo, payManualReport.getOrderNo());
        lqw.like(StringUtils.isNotBlank(payManualReport.getSupplierTradeNo()), PayManualReport::getSupplierTradeNo, payManualReport.getSupplierTradeNo());
        lqw.like(StringUtils.isNotBlank(payManualReport.getPayerName()), PayManualReport::getPayerName, payManualReport.getPayerName());
        lqw.orderByDesc(PayManualReport::getId);
        List<PayManualReport> list = payManualReportService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 人工审核支付上报。
     *
     * @param request 审核参数
     * @return 审核结果
     */
    @PostMapping("/audit")
    @Node(value = "payManualReport::audit", name = "支付上报人工审核")
    public R<Map<String, Object>> audit(@RequestBody PayManualReportAuditRequest request) {
        return R.success(payManualReportService.auditReport(request));
    }
}
