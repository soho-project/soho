package work.soho.pay.biz.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import work.soho.pay.biz.domain.PayManualReport;
import work.soho.pay.biz.domain.PayOrder;
import work.soho.pay.biz.enums.PayManualReportEnums;
import work.soho.pay.biz.request.PayManualReportAuditRequest;
import work.soho.pay.biz.service.PayInfoService;
import work.soho.pay.biz.service.PayOrderService;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 支付上报人工审核服务测试。
 */
@org.junit.runner.RunWith(MockitoJUnitRunner.class)
public class PayManualReportServiceImplTest {

    @Mock
    private PayInfoService payInfoService;

    @Mock
    private PayOrderService payOrderService;

    private PayManualReportServiceImpl payManualReportService;

    /**
     * 初始化被测对象。
     */
    @Before
    public void setUp() {
        payManualReportService = spy(new PayManualReportServiceImpl(payInfoService, payOrderService));
    }

    /**
     * 审核通过时应确认支付成功并更新上报记录状态。
     */
    @Test
    public void auditReportShouldApproveAndUpdateReportWhenRequestIsValid() {
        PayManualReport report = new PayManualReport();
        report.setId(1L);
        report.setMatchStatus(PayManualReportEnums.MatchStatus.WAIT_REVIEW.getCode());
        report.setSupplierTradeNo("SUP-001");
        report.setReportTime(LocalDateTime.now().minusMinutes(2));
        report.setOrderNo("PO-OLD");

        PayManualReportAuditRequest request = new PayManualReportAuditRequest();
        request.setReportId(1L);
        request.setApproved(true);
        request.setReviewer("admin_a");
        request.setTargetOrderNo("PO-NEW");
        request.setNote("人工核验通过");

        PayOrder payOrder = new PayOrder();
        payOrder.setId(99);
        payOrder.setOrderNo("PO-NEW");

        doReturn(report).when(payManualReportService).getById(1L);
        when(payOrderService.confirmOrderPaid("PO-NEW", "SUP-001", report.getReportTime())).thenReturn(true);
        when(payOrderService.getOne(any())).thenReturn(payOrder);
        doReturn(true).when(payManualReportService).updateById(any(PayManualReport.class));

        Map<String, Object> result = payManualReportService.auditReport(request);

        assertTrue((Boolean) result.get("success"));
        assertEquals("审核通过，订单已更新为支付成功", result.get("message"));
        assertEquals(Long.valueOf(1L), result.get("reportId"));
        verify(payOrderService).confirmOrderPaid("PO-NEW", "SUP-001", report.getReportTime());

        ArgumentCaptor<PayManualReport> captor = ArgumentCaptor.forClass(PayManualReport.class);
        verify(payManualReportService).updateById(captor.capture());
        PayManualReport updated = captor.getValue();
        assertEquals(Integer.valueOf(PayManualReportEnums.MatchStatus.MANUAL_APPROVED.getCode()), updated.getMatchStatus());
        assertEquals("人工核验通过", updated.getMatchNote());
        assertEquals("admin_a", updated.getReviewedBy());
        assertEquals("PO-NEW", updated.getOrderNo());
        assertEquals(Integer.valueOf(99), updated.getPayOrderId());
        assertNotNull(updated.getReviewedTime());
        assertNotNull(updated.getUpdatedTime());
    }

    /**
     * 审核通过但支付确认失败时应返回失败且不更新上报记录。
     */
    @Test
    public void auditReportShouldReturnFailWhenConfirmOrderPaidFailed() {
        PayManualReport report = new PayManualReport();
        report.setId(2L);
        report.setMatchStatus(PayManualReportEnums.MatchStatus.WAIT_REVIEW.getCode());
        report.setSupplierTradeNo("SUP-002");
        report.setReportTime(LocalDateTime.now().minusMinutes(1));
        report.setOrderNo("PO-FAIL");

        PayManualReportAuditRequest request = new PayManualReportAuditRequest();
        request.setReportId(2L);
        request.setApproved(true);
        request.setReviewer("admin_b");
        request.setTargetOrderNo("PO-FAIL");

        doReturn(report).when(payManualReportService).getById(2L);
        when(payOrderService.confirmOrderPaid(eq("PO-FAIL"), eq("SUP-002"), any(LocalDateTime.class))).thenReturn(false);

        Map<String, Object> result = payManualReportService.auditReport(request);

        assertFalse((Boolean) result.get("success"));
        assertEquals("目标支付单不存在或状态不可更新", result.get("message"));
        assertEquals(Long.valueOf(2L), result.get("reportId"));
        verify(payManualReportService, never()).updateById(any(PayManualReport.class));
    }
}
