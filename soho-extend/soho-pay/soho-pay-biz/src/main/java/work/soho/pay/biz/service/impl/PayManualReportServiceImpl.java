package work.soho.pay.biz.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.soho.common.core.util.StringUtils;
import work.soho.pay.biz.domain.PayInfo;
import work.soho.pay.biz.domain.PayManualReport;
import work.soho.pay.biz.domain.PayOrder;
import work.soho.pay.biz.enums.PayManualReportEnums;
import work.soho.pay.biz.mapper.PayManualReportMapper;
import work.soho.pay.biz.platform.model.PayOrderDetails;
import work.soho.pay.biz.platform.ndpay.adapter.NdpaySignUtil;
import work.soho.pay.biz.request.PayManualReportAuditRequest;
import work.soho.pay.biz.request.PayManualReportSubmitRequest;
import work.soho.pay.biz.service.PayInfoService;
import work.soho.pay.biz.service.PayManualReportService;
import work.soho.pay.biz.service.PayOrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义二维码支付上报服务实现。
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class PayManualReportServiceImpl extends ServiceImpl<PayManualReportMapper, PayManualReport>
        implements PayManualReportService {
    private static final String CUSTOM_QR_ADAPTER = "custom_qr";
    private static final int MAX_AUTO_MATCH_MINUTES_DIFF = 5;
    private static final int MAX_REPORT_DAYS_RANGE = 7;
    private static final long SIGN_EXPIRE_MILLIS = 10 * 60 * 1000L;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PayInfoService payInfoService;
    private final PayOrderService payOrderService;

    /**
     * 提交支付上报并执行自动匹配。
     *
     * @param request 上报参数
     * @return 处理结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitReport(PayManualReportSubmitRequest request) {
        String validateMessage = validateSubmitRequest(request);
        if (StringUtils.isNotBlank(validateMessage)) {
            return buildResult(false, validateMessage, null, false, false);
        }

        PayInfo payInfo = payInfoService.getById(request.getPayInfoId());
        if (payInfo == null) {
            return buildResult(false, "支付方式不存在", null, false, false);
        }
        if (!CUSTOM_QR_ADAPTER.equals(payInfo.getAdapterName())) {
            return buildResult(false, "该支付方式不支持二维码上报", null, false, false);
        }
        String signMessage = verifySubmitSignature(request, payInfo);
        if (StringUtils.isNotBlank(signMessage)) {
            return buildResult(false, signMessage, null, false, false);
        }

        PayManualReport duplicate = getOne(new LambdaQueryWrapper<PayManualReport>()
                .eq(PayManualReport::getPayId, request.getPayInfoId())
                .eq(PayManualReport::getSupplierTradeNo, request.getPayOrderNo())
                .last("limit 1"));
        if (duplicate != null) {
            return buildResult(false, "该支付方式下支付单号重复，请勿重复上报", duplicate.getId(), false, false);
        }

        List<PayOrder> candidates = payOrderService.list(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getPayId, request.getPayInfoId())
                .eq(PayOrder::getAmount, request.getPayAmount())
                .in(PayOrder::getStatus,
                        PayOrderDetails.TradeStateEnum.NOTPAY.getState(),
                        PayOrderDetails.TradeStateEnum.USERPAYING.getState())
                .le(PayOrder::getCreatedTime, request.getPayTime())
                .ge(PayOrder::getCreatedTime, request.getPayTime().minusMinutes(MAX_AUTO_MATCH_MINUTES_DIFF))
                .orderByDesc(PayOrder::getCreatedTime));
        boolean autoMatch = candidates.size() == 1;
        PayOrder matchedOrder = autoMatch ? candidates.get(0) : null;

        PayManualReport report = buildReportEntity(request, matchedOrder);
        save(report);
        report.setUpdatedTime(LocalDateTime.now());
        report.setMatchScore(autoMatch ? 100 : 0);

        if (autoMatch) {
            promoteOrderToUserPaying(matchedOrder);
            boolean confirmed = payOrderService.confirmOrderPaid(matchedOrder.getOrderNo(), request.getPayOrderNo(), request.getPayTime());
            if (confirmed) {
                report.setMatchStatus(PayManualReportEnums.MatchStatus.AUTO_MATCHED.getCode());
                report.setMatchNote("自动匹配成功：5分钟窗口内唯一订单命中");
                report.setReviewedBy("system");
                report.setReviewedTime(LocalDateTime.now());
                updateById(report);
                return buildResult(true, "自动匹配成功，订单已更新为支付成功", report.getId(), true, false);
            }
        }

        report.setMatchStatus(PayManualReportEnums.MatchStatus.WAIT_REVIEW.getCode());
        if (candidates.isEmpty()) {
            report.setMatchNote("未找到5分钟窗口内匹配订单，进入人工审核");
        } else {
            report.setMatchNote("存在多笔候选订单(" + candidates.size() + ")，进入人工审核");
        }
        updateById(report);
        return buildResult(true, "已提交，等待人工审核", report.getId(), false, true);
    }

    /**
     * 人工审核支付上报。
     *
     * @param request 审核参数
     * @return 审核结果
     */
    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public Map<String, Object> auditReport(PayManualReportAuditRequest request) {
        if (request == null || request.getReportId() == null || request.getApproved() == null) {
            return buildResult(false, "审核参数不完整", null, false, false);
        }

        PayManualReport report = getById(request.getReportId());
        if (report == null) {
            return buildResult(false, "上报记录不存在", null, false, false);
        }

        if (PayManualReportEnums.MatchStatus.AUTO_MATCHED.getCode() == safeInt(report.getMatchStatus())
                || PayManualReportEnums.MatchStatus.MANUAL_APPROVED.getCode() == safeInt(report.getMatchStatus())
                || PayManualReportEnums.MatchStatus.MANUAL_REJECTED.getCode() == safeInt(report.getMatchStatus())) {
            return buildResult(false, "当前记录已处理，不能重复审核", report.getId(), false, false);
        }

        report.setReviewedBy(StringUtils.isBlank(request.getReviewer()) ? "admin" : request.getReviewer());
        report.setReviewedTime(LocalDateTime.now());
        report.setUpdatedTime(LocalDateTime.now());

        if (Boolean.FALSE.equals(request.getApproved())) {
            report.setMatchStatus(PayManualReportEnums.MatchStatus.MANUAL_REJECTED.getCode());
            report.setMatchNote(StringUtils.isBlank(request.getNote()) ? "人工审核拒绝" : request.getNote());
            updateById(report);
            return buildResult(true, "审核已拒绝", report.getId(), false, false);
        }

        String targetOrderNo = StringUtils.isNotBlank(request.getTargetOrderNo()) ? request.getTargetOrderNo() : report.getOrderNo();
        if (StringUtils.isBlank(targetOrderNo)) {
            return buildResult(false, "缺少目标支付单号", report.getId(), false, false);
        }

        boolean confirmed = payOrderService.confirmOrderPaid(targetOrderNo, report.getSupplierTradeNo(), report.getReportTime());
        if (!confirmed) {
            return buildResult(false, "目标支付单不存在或状态不可更新", report.getId(), false, false);
        }

        PayOrder payOrder = payOrderService.getOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getOrderNo, targetOrderNo)
                .last("limit 1"));
        report.setPayOrderId(payOrder == null ? report.getPayOrderId() : payOrder.getId());
        report.setOrderNo(targetOrderNo);
        report.setMatchStatus(PayManualReportEnums.MatchStatus.MANUAL_APPROVED.getCode());
        report.setMatchNote(StringUtils.isBlank(request.getNote()) ? "人工审核通过" : request.getNote());
        updateById(report);
        return buildResult(true, "审核通过，订单已更新为支付成功", report.getId(), false, false);
    }

    /**
     * 基础参数校验。
     *
     * @param request 上报参数
     * @return 错误信息，为空表示校验通过
     */
    private String validateSubmitRequest(PayManualReportSubmitRequest request) {
        if (request == null) {
            return "请求参数不能为空";
        }
        if (request.getPayInfoId() == null) {
            return "payInfoId不能为空";
        }
        if (StringUtils.isBlank(request.getPayerName())) {
            return "付款人姓名不能为空";
        }
        if (request.getPayAmount() == null || request.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return "支付金额必须大于0";
        }
        if (request.getPayTime() == null) {
            return "支付时间不能为空";
        }
        if (request.getPayTime().isAfter(LocalDateTime.now().plusMinutes(5))) {
            return "支付时间不能晚于当前时间";
        }
        if (request.getPayTime().isBefore(LocalDateTime.now().minusDays(MAX_REPORT_DAYS_RANGE))) {
            return "支付时间超过可上报范围";
        }
        if (StringUtils.isBlank(request.getPayOrderNo())) {
            return "支付供应商单号不能为空";
        }
        if (StringUtils.isBlank(request.getSignTimestamp())) {
            return "signTimestamp不能为空";
        }
        if (StringUtils.isBlank(request.getSignNonce())) {
            return "signNonce不能为空";
        }
        if (StringUtils.isBlank(request.getSign())) {
            return "sign不能为空";
        }
        return "";
    }

    /**
     * 校验客户端上报签名。
     *
     * @param request 上报请求
     * @param payInfo 支付配置
     * @return 错误信息，为空表示通过
     */
    private String verifySubmitSignature(PayManualReportSubmitRequest request, PayInfo payInfo) {
        if (payInfo == null || StringUtils.isBlank(payInfo.getAccountPrivateKey())) {
            return "支付配置缺少签名密钥";
        }
        long signTs;
        try {
            signTs = Long.parseLong(request.getSignTimestamp());
        } catch (Exception ex) {
            return "signTimestamp格式错误";
        }
        long now = System.currentTimeMillis();
        if (Math.abs(now - signTs) > SIGN_EXPIRE_MILLIS) {
            return "签名已过期";
        }

        Map<String, Object> signMap = new HashMap<>();
        signMap.put("payInfoId", request.getPayInfoId());
        signMap.put("payAmount", normalizeAmount(request.getPayAmount()));
        signMap.put("payerName", request.getPayerName());
        signMap.put("payTime", request.getPayTime() == null ? "" : request.getPayTime().format(DATE_TIME_FORMATTER));
        signMap.put("payOrderNo", request.getPayOrderNo());
        signMap.put("remark", request.getRemark());
        signMap.put("signTimestamp", request.getSignTimestamp());
        signMap.put("signNonce", request.getSignNonce());
        boolean verified = NdpaySignUtil.verify(signMap, payInfo.getAccountPrivateKey(), request.getSign());
        return verified ? "" : "签名校验失败";
    }

    /**
     * 规范化金额字符串，避免签名时出现科学计数法。
     *
     * @param amount 金额
     * @return 金额字符串
     */
    private String normalizeAmount(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        return amount.stripTrailingZeros().toPlainString();
    }

    /**
     * 构建上报实体。
     *
     * @param request 上报参数
     * @param payOrder 候选支付单（可为空）
     * @return 上报实体
     */
    private PayManualReport buildReportEntity(PayManualReportSubmitRequest request, PayOrder payOrder) {
        PayManualReport report = new PayManualReport();
        report.setPayId(request.getPayInfoId());
        report.setPayOrderId(payOrder == null ? null : payOrder.getId());
        report.setOrderNo(payOrder == null ? null : payOrder.getOrderNo());
        report.setPayerName(request.getPayerName());
        report.setReportAmount(request.getPayAmount());
        report.setReportTime(request.getPayTime());
        report.setSupplierTradeNo(request.getPayOrderNo());
        report.setReportRemark(request.getRemark());
        report.setMatchStatus(PayManualReportEnums.MatchStatus.WAIT_MATCH.getCode());
        report.setMatchScore(0);
        report.setMatchNote("待自动匹配");
        report.setCreatedTime(LocalDateTime.now());
        report.setUpdatedTime(LocalDateTime.now());
        return report;
    }

    /**
     * 将支付单状态推进到用户支付中，便于前后端识别为已上报待确认。
     *
     * @param payOrder 支付单
     */
    private void promoteOrderToUserPaying(PayOrder payOrder) {
        if (payOrder == null || payOrder.getStatus() == null) {
            return;
        }
        if (payOrder.getStatus().intValue() == PayOrderDetails.TradeStateEnum.NOTPAY.getState()) {
            payOrder.setStatus(PayOrderDetails.TradeStateEnum.USERPAYING.getState());
            payOrder.setUpdatedTime(LocalDateTime.now());
            payOrderService.updateById(payOrder);
        }
    }

    /**
     * 构建统一返回结构。
     *
     * @param success 是否成功
     * @param message 提示信息
     * @param reportId 上报ID
     * @param autoMatched 是否自动匹配成功
     * @param needReview 是否需要人工审核
     * @return 返回结果
     */
    private Map<String, Object> buildResult(boolean success, String message, Long reportId, boolean autoMatched, boolean needReview) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", message);
        result.put("reportId", reportId);
        result.put("autoMatched", autoMatched);
        result.put("needReview", needReview);
        return result;
    }

    /**
     * 安全获取整数值。
     *
     * @param value 值
     * @return 整数
     */
    private int safeInt(Integer value) {
        return value == null ? -1 : value;
    }

}
