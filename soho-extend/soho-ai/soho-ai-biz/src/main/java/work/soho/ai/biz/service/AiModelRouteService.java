package work.soho.ai.biz.service;

import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiResolvedModelRoute;

import java.util.List;

/**
 * AI 模型路由服务。
 */
public interface AiModelRouteService {
    /**
     * 解析全局可用的模型路由。
     *
     * @param requestedModel 请求模型
     * @return 路由结果
     */
    AiResolvedModelRoute resolveRoute(String requestedModel);

    /**
     * 在指定提供方内解析模型路由。
     *
     * @param providerConfig 提供方配置
     * @param requestedModel 请求模型
     * @return 路由结果
     */
    AiResolvedModelRoute resolveRouteForProvider(AiProviderConfig providerConfig, String requestedModel);

    /**
     * 查询指定提供方可展示的模型列表。
     *
     * @param providerConfig 提供方配置
     * @return 模型列表
     */
    List<String> listDisplayModelsByProvider(AiProviderConfig providerConfig);

    /**
     * 查询全局可展示的模型列表。
     *
     * @return 模型列表
     */
    List<String> listDisplayModels();
}
