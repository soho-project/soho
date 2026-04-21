package work.soho.admin.biz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import work.soho.admin.api.dashboard.DashboardKvCardProvider;
import work.soho.admin.api.dashboard.DashboardListCardProvider;
import work.soho.admin.api.dashboard.DashboardNumberCardProvider;
import work.soho.admin.api.dashboard.DashboardUserCardProvider;
import work.soho.admin.api.vo.DashboardBuildContext;
import work.soho.admin.api.vo.DashboardIndexVo;
import work.soho.admin.api.vo.DashboardUserCardVo;
import work.soho.admin.api.vo.NumberCardVo;
import work.soho.admin.biz.service.DashboardService;
import work.soho.common.security.utils.SecurityUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dashboard 聚合服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final List<DashboardNumberCardProvider> dashboardNumberCardProviders;
    private final List<DashboardUserCardProvider> dashboardUserCardProviders;
    private final List<DashboardListCardProvider> dashboardListCardProviders;
    private final List<DashboardKvCardProvider> dashboardKvCardProviders;

    @Override
    public DashboardIndexVo index() {
        DashboardBuildContext context = new DashboardBuildContext(SecurityUtils.getLoginUserId());
        DashboardIndexVo result = new DashboardIndexVo();
        result.getNumbers().addAll(loadNumberCards(context));
        result.getListUserCard().addAll(loadUserCards(context));
        result.getListCards().addAll(loadListCards(context));
        result.getListKVCards().addAll(loadKvCards(context));
        return result;
    }

    /**
     * 加载数字卡片。
     *
     * @param context Dashboard 构建上下文
     * @return 数字卡片列表
     */
    private List<NumberCardVo> loadNumberCards(DashboardBuildContext context) {
        return dashboardNumberCardProviders.stream()
                .map(provider -> safeProvideNumberCards(provider, context))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * 加载用户卡片。
     *
     * @param context Dashboard 构建上下文
     * @return 用户卡片列表
     */
    private List<DashboardUserCardVo> loadUserCards(DashboardBuildContext context) {
        return dashboardUserCardProviders.stream()
                .map(provider -> safeProvideUserCards(provider, context))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * 加载列表卡片。
     *
     * @param context Dashboard 构建上下文
     * @return 列表卡片列表
     */
    private List<List> loadListCards(DashboardBuildContext context) {
        return dashboardListCardProviders.stream()
                .map(provider -> safeProvideListCards(provider, context))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * 加载键值卡片。
     *
     * @param context Dashboard 构建上下文
     * @return 键值卡片列表
     */
    private List<List> loadKvCards(DashboardBuildContext context) {
        return dashboardKvCardProviders.stream()
                .map(provider -> safeProvideKvCards(provider, context))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * 安全执行数字卡片提供者。
     *
     * @param provider 提供者
     * @param context 上下文
     * @return 数字卡片列表
     */
    private List<NumberCardVo> safeProvideNumberCards(DashboardNumberCardProvider provider, DashboardBuildContext context) {
        try {
            List<NumberCardVo> cards = provider.provide(context);
            return cards == null ? Collections.emptyList() : cards;
        } catch (Exception e) {
            log.error("load dashboard number cards error", e);
            return Collections.emptyList();
        }
    }

    /**
     * 安全执行用户卡片提供者。
     *
     * @param provider 提供者
     * @param context 上下文
     * @return 用户卡片列表
     */
    private List<DashboardUserCardVo> safeProvideUserCards(DashboardUserCardProvider provider, DashboardBuildContext context) {
        try {
            List<DashboardUserCardVo> cards = provider.provide(context);
            return cards == null ? Collections.emptyList() : cards;
        } catch (Exception e) {
            log.error("load dashboard user cards error", e);
            return Collections.emptyList();
        }
    }

    /**
     * 安全执行列表卡片提供者。
     *
     * @param provider 提供者
     * @param context 上下文
     * @return 列表卡片列表
     */
    private List<List> safeProvideListCards(DashboardListCardProvider provider, DashboardBuildContext context) {
        try {
            List<List> cards = provider.provide(context);
            return cards == null ? Collections.emptyList() : cards;
        } catch (Exception e) {
            log.error("load dashboard list cards error", e);
            return Collections.emptyList();
        }
    }

    /**
     * 安全执行键值卡片提供者。
     *
     * @param provider 提供者
     * @param context 上下文
     * @return 键值卡片列表
     */
    private List<List> safeProvideKvCards(DashboardKvCardProvider provider, DashboardBuildContext context) {
        try {
            List<List> cards = provider.provide(context);
            return cards == null ? Collections.emptyList() : cards;
        } catch (Exception e) {
            log.error("load dashboard kv cards error", e);
            return Collections.emptyList();
        }
    }
}
