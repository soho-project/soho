package work.soho.pay.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.pay.biz.domain.PayManualReport;
import work.soho.pay.biz.request.PayManualReportAuditRequest;
import work.soho.pay.biz.request.PayManualOrderPollRequest;
import work.soho.pay.biz.request.PayManualReportSubmitRequest;

import java.util.Map;

/**
 * 自定义二维码支付上报服务。
 */
public interface PayManualReportService extends IService<PayManualReport> {
    /**
     * 提交支付上报并触发自动匹配。
     *
     * @param request 上报参数
     * @return 处理结果
     */
    Map<String, Object> submitReport(PayManualReportSubmitRequest request);

    /**
     * 轮询自定义二维码支付方式下的新支付单。
     *
     * @param request 轮询参数
     * @return 轮询结果
     */
    Map<String, Object> pollPendingOrders(PayManualOrderPollRequest request);

    /**
     * 人工审核支付上报。
     *
     * @param request 审核参数
     * @return 审核结果
     */
    Map<String, Object> auditReport(PayManualReportAuditRequest request);
}
