package work.soho.ai.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.ai.biz.domain.AiProxyConfig;
import work.soho.ai.biz.utils.AiProxyLayerUtils;

import java.util.Optional;

/**
 * AI代理配置服务。
 */
public interface AiProxyConfigService extends IService<AiProxyConfig> {

    /**
     * 按供应商规则选择生效代理。
     *
     * 规则：
     * 1. 优先从与供应商绑定的代理中按权重选择；
     * 2. 若未命中，再从全局代理中按权重选择。
     *
     * @param provider 供应商编码
     * @return 选中的代理配置
     */
    Optional<AiProxyConfig> selectProxyByProvider(String provider);

    /**
     * 按供应商规则解析代理设置。
     *
     * @param provider 供应商编码
     * @return 代理设置
     */
    AiProxyLayerUtils.ProxySettings resolveProxySettings(String provider);
}
