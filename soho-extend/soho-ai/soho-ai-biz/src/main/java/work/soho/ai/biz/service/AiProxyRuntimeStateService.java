package work.soho.ai.biz.service;

import work.soho.ai.biz.domain.AiProxyConfig;

/**
 * AI 代理节点运行时状态服务。
 */
public interface AiProxyRuntimeStateService {

    /**
     * 判断当前代理节点是否允许继续接收请求。
     *
     * @param proxyConfig 代理配置
     * @return 是否允许请求
     */
    boolean isRequestAllowed(AiProxyConfig proxyConfig);

    /**
     * 计算代理节点当前有效权重。
     *
     * @param proxyConfig 代理配置
     * @return 动态有效权重
     */
    int getEffectiveWeight(AiProxyConfig proxyConfig);

    /**
     * 记录一次代理请求成功。
     *
     * @param proxyConfigId 代理配置ID
     * @param totalMs       总耗时
     */
    void recordSuccess(Long proxyConfigId, long totalMs);

    /**
     * 记录一次代理请求失败。
     *
     * @param proxyConfigId 代理配置ID
     * @param throwable     异常
     */
    void recordFailure(Long proxyConfigId, Throwable throwable);

    /**
     * 清理指定代理节点运行时状态。
     *
     * @param proxyConfigId 代理配置ID
     */
    void clearState(Long proxyConfigId);
}
