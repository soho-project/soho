package work.soho.ai.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import work.soho.ai.biz.domain.AiProxyConfig;
import work.soho.ai.biz.utils.AiProxyLayerUtils;

/**
 * 代理节点选择结果。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiProxySelectionResult {
    /**
     * 选中的代理配置。
     */
    private AiProxyConfig proxyConfig;

    /**
     * 解析后的代理设置。
     */
    private AiProxyLayerUtils.ProxySettings proxySettings;
}
