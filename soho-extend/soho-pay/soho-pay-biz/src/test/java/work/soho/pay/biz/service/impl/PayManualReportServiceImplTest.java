package work.soho.pay.biz.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import work.soho.pay.biz.domain.PayInfo;
import work.soho.pay.biz.domain.PayManualReport;
import work.soho.pay.biz.domain.PayOrder;
import work.soho.pay.biz.enums.PayManualReportEnums;
import work.soho.pay.biz.platform.ndpay.adapter.NdpaySignUtil;
import work.soho.pay.biz.request.PayManualReportAuditRequest;
import work.soho.pay.biz.request.PayManualOrderPollRequest;
import work.soho.pay.biz.service.PayInfoService;
import work.soho.pay.biz.service.PayManualOrderPollNotifier;
import work.soho.pay.biz.service.PayOrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    @Mock
    private PayManualOrderPollNotifier payManualOrderPollNotifier;

    private PayManualReportServiceImpl payManualReportService;

    /**
     * 初始化被测对象。
     */
    @Before
    public void setUp() {
        payManualReportService = spy(new PayManualReportServiceImpl(payInfoService, payOrderService, payManualOrderPollNotifier));
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

    /**
     * 轮询时应返回新的待处理支付单列表。
     */
    @Test
    public void pollPendingOrdersShouldReturnNewOrders() {
        PayManualOrderPollRequest request = buildPollRequest();

        PayInfo payInfo = new PayInfo();
        payInfo.setId(1001);
        payInfo.setAdapterName("custom_qr");
        payInfo.setAccountPrivateKey("secret");

        PayOrder first = buildPayOrder(11, "PO-11");
        PayOrder second = buildPayOrder(12, "PO-12");
        PayOrder third = buildPayOrder(13, "PO-13");
        List<PayOrder> payOrders = Arrays.asList(first, second, third);

        when(payInfoService.getById(1001)).thenReturn(payInfo);
        when(payOrderService.list(any())).thenReturn(payOrders);

        Map<String, Object> result = payManualReportService.pollPendingOrders(request);

        assertTrue((Boolean) result.get("success"));
        assertEquals("获取新支付单成功", result.get("message"));
        assertEquals(2, result.get("count"));
        assertEquals(12, result.get("nextOrderId"));
        assertEquals(true, result.get("hasMore"));

        List<?> orders = (List<?>) result.get("orders");
        assertEquals(2, orders.size());
    }

    /**
     * 轮询无新单时应返回空列表。
     */
    @Test
    public void pollPendingOrdersShouldReturnEmptyWhenNoNewOrder() {
        PayManualOrderPollRequest request = buildPollRequest();

        PayInfo payInfo = new PayInfo();
        payInfo.setId(1001);
        payInfo.setAdapterName("custom_qr");
        payInfo.setAccountPrivateKey("secret");

        when(payInfoService.getById(1001)).thenReturn(payInfo);
        when(payOrderService.list(any())).thenReturn(Collections.emptyList());
        when(payManualOrderPollNotifier.awaitNewOrder(1001, 25)).thenReturn(false);

        Map<String, Object> result = payManualReportService.pollPendingOrders(request);

        assertTrue((Boolean) result.get("success"));
        assertEquals("暂无新支付单", result.get("message"));
        assertEquals(0, result.get("count"));
        assertEquals(10, result.get("nextOrderId"));
        assertEquals(false, result.get("hasMore"));
        verify(payManualOrderPollNotifier).awaitNewOrder(1001, 25);
    }

    /**
     * 轮询签名错误时应直接返回失败。
     */
    @Test
    public void pollPendingOrdersShouldFailWhenSignInvalid() {
        PayManualOrderPollRequest request = buildPollRequest();
        request.setSign("BAD-SIGN");

        PayInfo payInfo = new PayInfo();
        payInfo.setId(1001);
        payInfo.setAdapterName("custom_qr");
        payInfo.setAccountPrivateKey("secret");

        when(payInfoService.getById(1001)).thenReturn(payInfo);

        Map<String, Object> result = payManualReportService.pollPendingOrders(request);

        assertFalse((Boolean) result.get("success"));
        assertEquals("签名校验失败", result.get("message"));
        verify(payOrderService, never()).list(any());
    }

    /**
     * 构建轮询请求。
     *
     * @return 轮询请求
     */
    private PayManualOrderPollRequest buildPollRequest() {
        PayManualOrderPollRequest request = new PayManualOrderPollRequest();
        request.setPayInfoId(1001);
        request.setLastOrderId(10);
        request.setLimit(2);
        request.setWaitSeconds(25);
        request.setSignTimestamp(String.valueOf(System.currentTimeMillis()));
        request.setSignNonce("nonce-001");
        request.setSign(buildPollSign(request, "secret"));
        return request;
    }

    /**
     * 构建轮询签名。
     *
     * @param request 轮询请求
     * @param key 签名密钥
     * @return 签名字符串
     */
    private String buildPollSign(PayManualOrderPollRequest request, String key) {
        Map<String, Object> signMap = new HashMap<>();
        signMap.put("payInfoId", request.getPayInfoId());
        signMap.put("lastOrderId", request.getLastOrderId());
        signMap.put("limit", request.getLimit());
        signMap.put("waitSeconds", request.getWaitSeconds());
        signMap.put("signTimestamp", request.getSignTimestamp());
        signMap.put("signNonce", request.getSignNonce());
        return NdpaySignUtil.sign(signMap, key);
    }

    /**
     * 构建支付单测试数据。
     *
     * @param id 支付单ID
     * @param orderNo 支付单号
     * @return 支付单
     */
    private PayOrder buildPayOrder(Integer id, String orderNo) {
        PayOrder payOrder = new PayOrder();
        payOrder.setId(id);
        payOrder.setPayId(1001);
        payOrder.setOrderNo(orderNo);
        payOrder.setTrackingNo("TRACK-" + orderNo);
        payOrder.setAmount(new BigDecimal("99.90"));
        payOrder.setStatus(3);
        payOrder.setCreatedTime(LocalDateTime.now());
        payOrder.setUpdatedTime(LocalDateTime.now());
        return payOrder;
    }
}
