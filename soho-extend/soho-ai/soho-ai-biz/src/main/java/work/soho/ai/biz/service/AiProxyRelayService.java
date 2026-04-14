package work.soho.ai.biz.service;

import work.soho.ai.biz.utils.AiProxyLayerUtils;

/**
 * AI 代理中继服务。
 */
public interface AiProxyRelayService {

    /**
     * 为需要本地中继的协议准备可用出口。
     *
     * @param settings 原始代理设置
     * @param provider 供应商编码
     * @return 可直接用于 HTTP 客户端的代理设置
     */
    AiProxyLayerUtils.ProxySettings ensureRelay(AiProxyLayerUtils.ProxySettings settings, String provider);
}

