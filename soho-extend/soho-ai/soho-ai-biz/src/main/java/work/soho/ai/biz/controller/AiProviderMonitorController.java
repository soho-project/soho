package work.soho.ai.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiApiCallLogProviderConfigStatsDTO;
import work.soho.ai.biz.dto.AiProviderMonitorViewDTO;
import work.soho.ai.biz.dto.AiProviderRuntimeStateSnapshotDTO;
import work.soho.ai.biz.service.AiApiCallLogService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderRuntimeStateService;
import work.soho.common.core.result.R;
import work.soho.common.security.annotation.Node;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 提供方监控 Controller。
 */
@Api(value = "AI提供方监控", tags = "AI提供方监控")
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/admin/aiProviderMonitor")
public class AiProviderMonitorController {

    private final AiProviderConfigService aiProviderConfigService;
    private final AiApiCallLogService aiApiCallLogService;
    private final AiProviderRuntimeStateService aiProviderRuntimeStateService;

    /**
     * 获取提供方监控列表。
     *
     * @return 监控列表
     */
    @GetMapping("/list")
    @Node(value = "aiProviderMonitor::list", name = "获取 AI提供方监控列表")
    @ApiOperation(value = "获取 AI提供方监控列表", notes = "返回提供方基础配置、运行态状态以及调用统计")
    public R<List<AiProviderMonitorViewDTO>> list() {
        List<AiProviderConfig> providerConfigs = aiProviderConfigService.list(new LambdaQueryWrapper<AiProviderConfig>()
                .orderByDesc(AiProviderConfig::getStatus)
                .orderByDesc(AiProviderConfig::getWeight)
                .orderByAsc(AiProviderConfig::getId));
        Map<Long, AiApiCallLogProviderConfigStatsDTO> todayStatsMap = toStatsMap(aiApiCallLogService.statisticsTodayByProviderConfig());
        Map<Long, AiApiCallLogProviderConfigStatsDTO> totalStatsMap = toStatsMap(aiApiCallLogService.statisticsTotalByProviderConfig());

        List<AiProviderMonitorViewDTO> result = new ArrayList<>();
        for (AiProviderConfig providerConfig : providerConfigs) {
            result.add(buildMonitorView(providerConfig, todayStatsMap.get(providerConfig.getId()), totalStatsMap.get(providerConfig.getId())));
        }
        return R.success(result);
    }

    /**
     * 构建监控视图。
     *
     * @param providerConfig 提供方配置
     * @param todayStats 今日统计
     * @param totalStats 累计统计
     * @return 监控视图
     */
    private AiProviderMonitorViewDTO buildMonitorView(AiProviderConfig providerConfig,
                                                      AiApiCallLogProviderConfigStatsDTO todayStats,
                                                      AiApiCallLogProviderConfigStatsDTO totalStats) {
        AiProviderRuntimeStateSnapshotDTO runtimeSnapshot = aiProviderRuntimeStateService.getSnapshot(providerConfig);
        AiProviderMonitorViewDTO view = new AiProviderMonitorViewDTO();
        view.setProviderConfigId(providerConfig.getId());
        view.setProviderCode(providerConfig.getCode());
        view.setProvider(providerConfig.getProvider());
        view.setEnv(providerConfig.getEnv());
        view.setDefaultModel(providerConfig.getDefaultModel());
        view.setStatus(providerConfig.getStatus());
        view.setBaseWeight(providerConfig.getWeight());
        view.setEffectiveWeight(runtimeSnapshot.getEffectiveWeight());
        view.setRequestAllowed(runtimeSnapshot.isRequestAllowed());
        view.setCircuitOpenUntilMs(runtimeSnapshot.getCircuitOpenUntilMs());
        view.setLastSuccessAtMs(runtimeSnapshot.getLastSuccessAtMs());
        view.setLastFailureAtMs(runtimeSnapshot.getLastFailureAtMs());
        view.setEwmaTotalMs(runtimeSnapshot.getEwmaTotalMs());
        view.setEwmaFirstTokenMs(runtimeSnapshot.getEwmaFirstTokenMs());
        view.setConsecutiveFailures(runtimeSnapshot.getConsecutiveFailures());
        view.setConsecutiveSlowRequests(runtimeSnapshot.getConsecutiveSlowRequests());
        view.setLastErrorMessage(runtimeSnapshot.getLastErrorMessage());
        fillStats(view, todayStats, true);
        fillStats(view, totalStats, false);
        return view;
    }

    /**
     * 填充统计信息。
     *
     * @param view 监控视图
     * @param stats 统计数据
     * @param today 是否今日统计
     */
    private void fillStats(AiProviderMonitorViewDTO view, AiApiCallLogProviderConfigStatsDTO stats, boolean today) {
        long requestCount = stats == null || stats.getRequestCount() == null ? 0L : stats.getRequestCount();
        long promptTokens = stats == null || stats.getPromptTokens() == null ? 0L : stats.getPromptTokens();
        long completionTokens = stats == null || stats.getCompletionTokens() == null ? 0L : stats.getCompletionTokens();
        long totalTokens = stats == null || stats.getTotalTokens() == null ? 0L : stats.getTotalTokens();
        if (today) {
            view.setTodayRequestCount(requestCount);
            view.setTodayPromptTokens(promptTokens);
            view.setTodayCompletionTokens(completionTokens);
            view.setTodayTotalTokens(totalTokens);
            return;
        }
        view.setTotalRequestCount(requestCount);
        view.setTotalPromptTokens(promptTokens);
        view.setTotalCompletionTokens(completionTokens);
        view.setTotalTotalTokens(totalTokens);
    }

    /**
     * 将统计列表转换为 Map。
     *
     * @param statsList 统计列表
     * @return 统计 Map
     */
    private Map<Long, AiApiCallLogProviderConfigStatsDTO> toStatsMap(List<AiApiCallLogProviderConfigStatsDTO> statsList) {
        Map<Long, AiApiCallLogProviderConfigStatsDTO> statsMap = new LinkedHashMap<>();
        if (statsList == null) {
            return statsMap;
        }
        for (AiApiCallLogProviderConfigStatsDTO item : statsList) {
            if (item != null && item.getProviderConfigId() != null) {
                statsMap.put(item.getProviderConfigId(), item);
            }
        }
        return statsMap;
    }
}
