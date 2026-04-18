package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiProxyConfig;
import work.soho.ai.biz.dto.AiProxySelectionResult;
import work.soho.ai.biz.mapper.AiProxyConfigMapper;
import work.soho.ai.biz.service.AiProxyConfigService;
import work.soho.ai.biz.service.AiProxyRuntimeStateService;
import work.soho.ai.biz.utils.AiProxyLayerUtils;
import work.soho.common.core.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * AI代理配置服务实现。
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class AiProxyConfigServiceImpl extends ServiceImpl<AiProxyConfigMapper, AiProxyConfig>
        implements AiProxyConfigService {
    private final AiProxyRuntimeStateService aiProxyRuntimeStateService;

    /**
     * 按供应商优先 + 权重随机选择代理。
     *
     * @param provider 供应商编码
     * @return 选中的代理配置
     */
    @Override
    public Optional<AiProxyConfig> selectProxyByProvider(String provider) {
        String normalizedProvider = normalizeProvider(provider);
        List<AiProxyConfig> providerBound = listByProvider(normalizedProvider);
        AiProxyConfig selected = selectByWeight(providerBound);
        if (selected != null) {
            return Optional.of(selected);
        }
        List<AiProxyConfig> global = listGlobal();
        selected = selectByWeight(global);
        if (selected != null) {
            return Optional.of(selected);
        }
        return Optional.empty();
    }

    /**
     * 按供应商规则选择并解析代理节点。
     *
     * @param provider 供应商编码
     * @return 选择结果
     */
    @Override
    public AiProxySelectionResult resolveProxySelection(String provider) {
        Optional<AiProxyConfig> optional = selectProxyByProvider(provider);
        if (optional.isEmpty()) {
            return null;
        }
        AiProxyConfig selected = optional.get();
        Map<String, Object> configMap = toProxyConfigMap(selected);
        AiProxyLayerUtils.ProxySettings settings = AiProxyLayerUtils.resolve(configMap);
        log.info("proxy node selected from table, provider={}, node={}", normalizeProvider(provider), summarizeNode(selected));
        return new AiProxySelectionResult(selected, settings);
    }

    /**
     * 解析供应商可用的代理设置。
     *
     * @param provider 供应商编码
     * @return 代理设置
     */
    @Override
    public AiProxyLayerUtils.ProxySettings resolveProxySettings(String provider) {
        AiProxySelectionResult result = resolveProxySelection(provider);
        return result == null ? null : result.getProxySettings();
    }

    /**
     * 查询与供应商绑定的启用代理。
     *
     * @param provider 供应商编码
     * @return 代理列表
     */
    private List<AiProxyConfig> listByProvider(String provider) {
        if (StringUtils.isBlank(provider)) {
            return Collections.emptyList();
        }
        return list(new LambdaQueryWrapper<AiProxyConfig>()
                .eq(AiProxyConfig::getStatus, 1)
                .eq(AiProxyConfig::getProvider, provider)
                .orderByDesc(AiProxyConfig::getWeight)
                .orderByAsc(AiProxyConfig::getId));
    }

    /**
     * 查询全局启用代理。
     *
     * @return 代理列表
     */
    private List<AiProxyConfig> listGlobal() {
        return list(new LambdaQueryWrapper<AiProxyConfig>()
                .eq(AiProxyConfig::getStatus, 1)
                .and(wrapper -> wrapper.isNull(AiProxyConfig::getProvider).or().eq(AiProxyConfig::getProvider, ""))
                .orderByDesc(AiProxyConfig::getWeight)
                .orderByAsc(AiProxyConfig::getId));
    }

    /**
     * 按权重随机选择代理。
     *
     * @param candidates 候选代理列表
     * @return 选中的代理
     */
    private AiProxyConfig selectByWeight(List<AiProxyConfig> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        int totalWeight = 0;
        for (AiProxyConfig item : candidates) {
            totalWeight += normalizedWeight(item);
        }
        if (totalWeight <= 0) {
            return null;
        }
        int hit = ThreadLocalRandom.current().nextInt(totalWeight);
        int current = 0;
        for (AiProxyConfig item : candidates) {
            current += normalizedWeight(item);
            if (hit < current) {
                return item;
            }
        }
        return candidates.get(candidates.size() - 1);
    }

    /**
     * 归一化供应商编码。
     *
     * @param provider 原始供应商编码
     * @return 归一化后的编码
     */
    private String normalizeProvider(String provider) {
        if (StringUtils.isBlank(provider)) {
            return "";
        }
        return provider.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * 归一化权重，确保最小权重为1。
     *
     * @param config 代理配置
     * @return 归一化权重
     */
    private int normalizedWeight(AiProxyConfig config) {
        return aiProxyRuntimeStateService.getEffectiveWeight(config);
    }

    /**
     * 将实体转成代理层工具可识别的配置Map。
     *
     * @param config 代理配置实体
     * @return 配置Map
     */
    private Map<String, Object> toProxyConfigMap(AiProxyConfig config) {
        Map<String, Object> map = new HashMap<>();
        if (config == null) {
            return map;
        }
        map.put("proxyNodeId", config.getId());
        putIfNotBlank(map, "proxyNodeName", config.getName());
        putIfNotBlank(map, "proxyNodeProvider", config.getProvider());
        putIfNotBlank(map, "proxyType", config.getProxyType());
        putIfNotBlank(map, "proxyHost", config.getProxyHost());
        if (config.getProxyPort() != null) {
            map.put("proxyPort", config.getProxyPort());
        }
        putIfNotBlank(map, "proxyUrl", config.getProxyUrl());
        putIfNotBlank(map, "proxyUsername", config.getProxyUsername());
        putIfNotBlank(map, "proxyPassword", config.getProxyPassword());
        return map;
    }

    /**
     * 仅在值非空时写入Map。
     *
     * @param map 目标Map
     * @param key 字段
     * @param value 值
     */
    private void putIfNotBlank(Map<String, Object> map, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            map.put(key, value.trim());
        }
    }

    /**
     * 汇总节点关键字段，便于错误定位。
     *
     * @param config 代理配置
     * @return 节点摘要
     */
    private String summarizeNode(AiProxyConfig config) {
        if (config == null) {
            return "{}";
        }
        return "{id=" + config.getId()
                + ",name=" + config.getName()
                + ",provider=" + config.getProvider()
                + ",type=" + config.getProxyType()
                + ",host=" + config.getProxyHost()
                + ",port=" + config.getProxyPort()
                + "}";
    }
}
