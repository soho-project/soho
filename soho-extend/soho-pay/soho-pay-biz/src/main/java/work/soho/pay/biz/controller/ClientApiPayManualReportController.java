package work.soho.pay.biz.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.common.core.result.R;
import work.soho.pay.biz.request.PayManualOrderPollRequest;
import work.soho.pay.biz.request.PayManualReportSubmitRequest;
import work.soho.pay.biz.service.PayManualReportService;

import java.util.Map;

/**
 * 客户端自定义二维码支付上报接口。
 */
@Api(tags = "客户端支付上报")
@RequiredArgsConstructor
@RestController
@RequestMapping("/pay/guest/api/pay/customQr")
public class ClientApiPayManualReportController {
    private final PayManualReportService payManualReportService;

    /**
     * 提交支付上报。
     *
     * @param request 上报参数
     * @return 上报结果
     */
    @PostMapping("/report")
    public R<Map<String, Object>> submitReport(@RequestBody PayManualReportSubmitRequest request) {
        return R.success(payManualReportService.submitReport(request));
    }

    /**
     * 支付服务器轮询新创建的支付单。
     *
     * @param request 轮询参数
     * @return 轮询结果
     */
    @GetMapping("/pollOrders")
    public R<Map<String, Object>> pollOrders(PayManualOrderPollRequest request) {
        return R.success(payManualReportService.pollPendingOrders(request));
    }
}
