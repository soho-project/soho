package work.soho.ai.biz.service;

import work.soho.ai.biz.dto.AiProxyRuntimeStateSnapshot;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * AI 代理节点运行时状态仓储。
 */
public interface AiProxyRuntimeStateRepository {

    /**
     * 保存指定代理节点运行时状态。
     *
     * @param snapshot 运行时状态快照
     */
    void save(AiProxyRuntimeStateSnapshot snapshot);

    /**
     * 读取指定代理节点运行时状态。
     *
     * @param proxyConfigId 代理配置ID
     * @return 运行时状态
     */
    Optional<AiProxyRuntimeStateSnapshot> findById(Long proxyConfigId);

    /**
     * 批量读取代理节点运行时状态。
     *
     * @param proxyConfigIds 代理配置ID集合
     * @return 状态映射
     */
    Map<Long, AiProxyRuntimeStateSnapshot> findByIds(Collection<Long> proxyConfigIds);

    /**
     * 删除指定代理节点运行时状态。
     *
     * @param proxyConfigId 代理配置ID
     */
    void delete(Long proxyConfigId);
}
