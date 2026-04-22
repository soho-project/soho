package work.soho.pay.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.pay.biz.domain.PayInfo;
import work.soho.pay.biz.dto.PayDashboardHourAmountStatsDTO;
import work.soho.pay.biz.dto.PayDashboardOverviewStatsDTO;
import work.soho.pay.biz.dto.PayDashboardPayMethodHourAmountStatsDTO;
import work.soho.pay.biz.mapper.PayOrderMapper;
import work.soho.pay.biz.platform.model.PayOrderDetails;
import work.soho.pay.biz.service.PayDashboardService;
import work.soho.pay.biz.service.PayInfoService;
import work.soho.pay.biz.vo.PayDashboardHourAmountVo;
import work.soho.pay.biz.vo.PayDashboardIndexVo;
import work.soho.pay.biz.vo.PayDashboardOverviewVo;
import work.soho.pay.biz.vo.PayDashboardPayMethodTrendVo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支付看板服务实现。
 */
@Service
@RequiredArgsConstructor
public class PayDashboardServiceImpl implements PayDashboardService {
    private static final int ENABLED_STATUS = 1;
    private static final int DISABLED_STATUS = 0;

    private final PayOrderMapper payOrderMapper;
    private final PayInfoService payInfoService;

    /**
     * 获取支付模块 Dashboard 首页数据。
     *
     * @return 支付看板首页数据
     */
    @Override
    public PayDashboardIndexVo index() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowStart = todayStart.plusDays(1);
        List<PayInfo> payInfos = listPayInfos();

        PayDashboardIndexVo result = new PayDashboardIndexVo();
        result.setOverview(buildOverview(todayStart, tomorrowStart, payInfos));
        result.setTodayAmountTrend(buildTodayAmountTrend(todayStart, tomorrowStart));
        result.setPayMethodAmountTrends(buildPayMethodAmountTrends(todayStart, tomorrowStart, payInfos));
        return result;
    }

    /**
     * 构造看板概览数据。
     *
     * @param todayStart 今日开始时间
     * @param tomorrowStart 明日开始时间
     * @param payInfos 支付方式列表
     * @return 概览数据
     */
    private PayDashboardOverviewVo buildOverview(LocalDateTime todayStart, LocalDateTime tomorrowStart, List<PayInfo> payInfos) {
        PayDashboardOverviewStatsDTO stats = payOrderMapper.selectTodayOverview(
                todayStart,
                tomorrowStart,
                PayOrderDetails.TradeStateEnum.SUCCESS.getState(),
                PayOrderDetails.TradeStateEnum.NOTPAY.getState()
        );

        PayDashboardOverviewVo overview = new PayDashboardOverviewVo();
        overview.setTodayPaidAmount(defaultAmount(stats == null ? null : stats.getTodayPaidAmount()));
        overview.setTodayPaidOrderCount(defaultLong(stats == null ? null : stats.getTodayPaidOrderCount()));
        overview.setTodayUnpaidOrderCount(defaultLong(stats == null ? null : stats.getTodayUnpaidOrderCount()));
        overview.setPayMethodTotalCount((long) payInfos.size());
        overview.setEnabledPayMethodCount(countPayMethodsByStatus(ENABLED_STATUS));
        overview.setDisabledPayMethodCount(countPayMethodsByStatus(DISABLED_STATUS));
        return overview;
    }

    /**
     * 构造今日总支付金额分时曲线。
     *
     * @param todayStart 今日开始时间
     * @param tomorrowStart 明日开始时间
     * @return 分时曲线
     */
    private List<PayDashboardHourAmountVo> buildTodayAmountTrend(LocalDateTime todayStart, LocalDateTime tomorrowStart) {
        List<PayDashboardHourAmountStatsDTO> stats = payOrderMapper.selectTodayPaidAmountByHour(
                todayStart,
                tomorrowStart,
                PayOrderDetails.TradeStateEnum.SUCCESS.getState()
        );
        return buildHourAmountPoints(stats.stream()
                .collect(Collectors.toMap(PayDashboardHourAmountStatsDTO::getHour, item -> defaultAmount(item.getAmount()))));
    }

    /**
     * 构造按支付方式分组的今日分时金额曲线。
     *
     * @param todayStart 今日开始时间
     * @param tomorrowStart 明日开始时间
     * @param payInfos 支付方式列表
     * @return 支付方式分时曲线列表
     */
    private List<PayDashboardPayMethodTrendVo> buildPayMethodAmountTrends(LocalDateTime todayStart, LocalDateTime tomorrowStart,
                                                                          List<PayInfo> payInfos) {
        List<PayDashboardPayMethodHourAmountStatsDTO> stats = payOrderMapper.selectTodayPaidAmountByHourAndPayInfo(
                todayStart,
                tomorrowStart,
                PayOrderDetails.TradeStateEnum.SUCCESS.getState()
        );
        Map<Integer, Map<Integer, BigDecimal>> amountByPayAndHour = buildAmountByPayAndHour(stats);

        List<PayDashboardPayMethodTrendVo> result = new ArrayList<>();
        for (PayInfo payInfo : payInfos) {
            PayDashboardPayMethodTrendVo trend = new PayDashboardPayMethodTrendVo();
            trend.setPayId(payInfo.getId());
            trend.setPayTitle(resolvePayTitle(payInfo));
            trend.setPoints(buildHourAmountPoints(amountByPayAndHour.getOrDefault(payInfo.getId(), Map.of())));
            result.add(trend);
        }
        return result;
    }

    /**
     * 构造支付方式与小时的金额映射。
     *
     * @param stats 原始统计结果
     * @return 支付方式与小时金额映射
     */
    private Map<Integer, Map<Integer, BigDecimal>> buildAmountByPayAndHour(List<PayDashboardPayMethodHourAmountStatsDTO> stats) {
        Map<Integer, Map<Integer, BigDecimal>> result = new LinkedHashMap<>();
        for (PayDashboardPayMethodHourAmountStatsDTO item : stats) {
            result.computeIfAbsent(item.getPayId(), key -> new LinkedHashMap<>())
                    .put(item.getHour(), defaultAmount(item.getAmount()));
        }
        return result;
    }

    /**
     * 构造 24 小时金额点位，缺失小时自动补零。
     *
     * @param amountByHour 小时金额映射
     * @return 24 小时点位列表
     */
    private List<PayDashboardHourAmountVo> buildHourAmountPoints(Map<Integer, BigDecimal> amountByHour) {
        List<PayDashboardHourAmountVo> result = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            result.add(new PayDashboardHourAmountVo(formatHour(hour), defaultAmount(amountByHour.get(hour))));
        }
        return result;
    }

    /**
     * 统计指定状态的支付方式数量。
     *
     * @param status 支付方式状态
     * @return 数量
     */
    private Long countPayMethodsByStatus(Integer status) {
        LambdaQueryWrapper<PayInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PayInfo::getStatus, status);
        return payInfoService.count(wrapper);
    }

    /**
     * 查询全部支付方式并按 ID 升序排序。
     *
     * @return 支付方式列表
     */
    private List<PayInfo> listPayInfos() {
        return payInfoService.list().stream()
                .sorted(Comparator.comparing(PayInfo::getId))
                .collect(Collectors.toList());
    }

    /**
     * 解析支付方式展示标题。
     *
     * @param payInfo 支付方式
     * @return 展示标题
     */
    private String resolvePayTitle(PayInfo payInfo) {
        if (payInfo.getTitle() != null && !payInfo.getTitle().trim().isEmpty()) {
            return payInfo.getTitle();
        }
        if (payInfo.getName() != null && !payInfo.getName().trim().isEmpty()) {
            return payInfo.getName();
        }
        return "支付方式#" + payInfo.getId();
    }

    /**
     * 将小时数字格式化为 HH:00。
     *
     * @param hour 小时
     * @return 小时标签
     */
    private String formatHour(int hour) {
        return String.format("%02d:00", hour);
    }

    /**
     * 处理空金额默认值。
     *
     * @param amount 原金额
     * @return 非空金额
     */
    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    /**
     * 处理空整型统计默认值。
     *
     * @param value 原值
     * @return 非空统计值
     */
    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }
}
