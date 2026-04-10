package work.soho.ai.biz.service;

import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.dto.AiProviderRuntimeStateSnapshotDTO;

/**
 * AI 提供方运行时状态服务。
 */
public interface AiProviderRuntimeStateService {

    /**
     * 判断当前提供方是否允许继续接收请求。
     *
     * @param providerConfig 提供方配置
     * @return 是否允许请求
     */
    boolean isRequestAllowed(AiProviderConfig providerConfig);

    /**
     * 计算运行时动态权重。
     *
     * @param providerConfig 提供方配置
     * @return 动态权重
     */
    int getEffectiveWeight(AiProviderConfig providerConfig);

    /**
     * 记录一次上游成功。
     *
     * @param providerConfig 提供方配置
     * @param totalMs 总耗时
     * @param firstTokenMs 首字耗时
     */
    void recordSuccess(AiProviderConfig providerConfig, long totalMs, Long firstTokenMs);

    /**
     * 记录一次上游失败。
     *
     * @param providerConfig 提供方配置
     * @param throwable 异常
     */
    void recordFailure(AiProviderConfig providerConfig, Throwable throwable);

    /**
     * 读取当前提供方运行时快照。
     *
     * @param providerConfig 提供方配置
     * @return 运行时快照
     */
    AiProviderRuntimeStateSnapshotDTO getSnapshot(AiProviderConfig providerConfig);
}
