package work.soho.ai.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiResolvedModelRoute;
import work.soho.ai.biz.service.AiModelInfoService;
import work.soho.ai.biz.service.AiModelRouteService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.ai.biz.utils.AiProviderModelUtils;
import work.soho.common.core.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * AI 模型路由服务实现。
 */
@Service
@RequiredArgsConstructor
public class AiModelRouteServiceImpl implements AiModelRouteService {
    private final AiModelInfoService aiModelInfoService;
    private final AiProviderConfigService aiProviderConfigService;
    private final AiProviderModelRelService aiProviderModelRelService;

    /**
     * 解析全局可用的模型路由。
     *
     * @param requestedModel 请求模型
     * @return 路由结果
     */
    @Override
    public AiResolvedModelRoute resolveRoute(String requestedModel) {
        return doResolveRoute(requestedModel, null);
    }

    /**
     * 在指定提供方内解析模型路由。
     *
     * @param providerConfig 提供方配置
     * @param requestedModel 请求模型
     * @return 路由结果
     */
    @Override
    public AiResolvedModelRoute resolveRouteForProvider(AiProviderConfig providerConfig, String requestedModel) {
        return doResolveRoute(requestedModel, providerConfig);
    }

    /**
     * 查询指定提供方可展示的模型列表。
     *
     * @param providerConfig 提供方配置
     * @return 模型列表
     */
    @Override
    public List<String> listDisplayModelsByProvider(AiProviderConfig providerConfig) {
        LinkedHashSet<String> models = new LinkedHashSet<>(listDirectModels(providerConfig));
        for (AiModelInfo modelInfo : aiModelInfoService.listEnabledModels()) {
            String modelName = modelInfo.getModelName();
            if (StringUtils.isBlank(modelName)) {
                continue;
            }
            AiResolvedModelRoute route = resolveRouteForProvider(providerConfig, modelName);
            if (StringUtils.isNotBlank(route.getActualModel())) {
                models.add(modelName);
            }
        }
        return new ArrayList<>(models);
    }

    /**
     * 查询全局可展示的模型列表。
     *
     * @return 模型列表
     */
    @Override
    public List<String> listDisplayModels() {
        LinkedHashSet<String> models = new LinkedHashSet<>();
        for (AiModelInfo modelInfo : aiModelInfoService.listEnabledModels()) {
            String modelName = modelInfo.getModelName();
            if (StringUtils.isBlank(modelName)) {
                continue;
            }
            AiResolvedModelRoute route = resolveRoute(modelName);
            if (StringUtils.isNotBlank(route.getActualModel())) {
                models.add(modelName);
            }
        }
        return new ArrayList<>(models);
    }

    /**
     * 执行模型兜底解析。
     *
     * @param requestedModel 请求模型
     * @param providerConfig 指定提供方；为空表示全局
     * @return 路由结果
     */
    private AiResolvedModelRoute doResolveRoute(String requestedModel, AiProviderConfig providerConfig) {
        AiResolvedModelRoute route = new AiResolvedModelRoute();
        String normalizedRequestedModel = normalizeModelName(requestedModel);
        route.setRequestedModel(normalizedRequestedModel);
        if (StringUtils.isBlank(normalizedRequestedModel)) {
            return route;
        }
        LinkedHashSet<String> visitedModels = new LinkedHashSet<>();
        ArrayList<String> chain = new ArrayList<>();
        String currentModel = normalizedRequestedModel;
        while (StringUtils.isNotBlank(currentModel)) {
            if (!visitedModels.add(currentModel)) {
                chain.add(currentModel);
                throw new IllegalStateException("模型兜底配置存在循环: " + String.join(" -> ", chain));
            }
            chain.add(currentModel);
            if (supportsModel(providerConfig, currentModel)) {
                route.setActualModel(currentModel);
                route.setFallbackApplied(!normalizedRequestedModel.equals(currentModel));
                route.setFallbackChain(new ArrayList<>(chain));
                return route;
            }
            AiModelInfo currentModelInfo = aiModelInfoService.findEnabledByModelName(currentModel);
            if (currentModelInfo == null || currentModelInfo.getFallbackModelId() == null) {
                route.setFallbackChain(new ArrayList<>(chain));
                return route;
            }
            AiModelInfo fallbackModelInfo = aiModelInfoService.findEnabledById(currentModelInfo.getFallbackModelId());
            if (fallbackModelInfo == null || StringUtils.isBlank(fallbackModelInfo.getModelName())) {
                route.setFallbackChain(new ArrayList<>(chain));
                return route;
            }
            currentModel = normalizeModelName(fallbackModelInfo.getModelName());
        }
        route.setFallbackChain(new ArrayList<>(chain));
        return route;
    }

    /**
     * 判断模型是否可被当前范围路由。
     *
     * @param providerConfig 指定提供方；为空表示全局
     * @param modelName 模型名
     * @return 是否可路由
     */
    private boolean supportsModel(AiProviderConfig providerConfig, String modelName) {
        if (StringUtils.isBlank(modelName)) {
            return false;
        }
        if (providerConfig != null) {
            return supportsModelByProvider(providerConfig, modelName);
        }
        for (AiProviderConfig enabledProviderConfig : aiProviderConfigService.listEnabledProviderConfigs()) {
            if (supportsModelByProvider(enabledProviderConfig, modelName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断指定提供方是否直接支持该模型。
     *
     * @param providerConfig 提供方配置
     * @param modelName 模型名
     * @return 是否支持
     */
    private boolean supportsModelByProvider(AiProviderConfig providerConfig, String modelName) {
        if (providerConfig == null || providerConfig.getId() == null || StringUtils.isBlank(modelName)) {
            return false;
        }
        return listDirectModels(providerConfig).contains(modelName);
    }

    /**
     * 查询提供方直接声明的模型列表。
     *
     * @param providerConfig 提供方配置
     * @return 模型列表
     */
    private List<String> listDirectModels(AiProviderConfig providerConfig) {
        LinkedHashSet<String> models = new LinkedHashSet<>();
        if (providerConfig == null || providerConfig.getId() == null) {
            return new ArrayList<>(models);
        }
        List<AiModelInfo> relModels = aiProviderModelRelService.listEnabledModelsByProviderConfigId(providerConfig.getId());
        if (!relModels.isEmpty()) {
            for (AiModelInfo relModel : relModels) {
                String modelName = normalizeModelName(relModel.getModelName());
                if (StringUtils.isNotBlank(modelName)) {
                    models.add(modelName);
                }
            }
            return new ArrayList<>(models);
        }
        for (String modelName : AiProviderModelUtils.extractModels(providerConfig)) {
            String normalizedModelName = normalizeModelName(modelName);
            if (StringUtils.isNotBlank(normalizedModelName)) {
                models.add(normalizedModelName);
            }
        }
        return new ArrayList<>(models);
    }

    /**
     * 规范化模型名。
     *
     * @param modelName 模型名
     * @return 规范化后的模型名
     */
    private String normalizeModelName(String modelName) {
        return StringUtils.isBlank(modelName) ? null : modelName.trim();
    }
}
