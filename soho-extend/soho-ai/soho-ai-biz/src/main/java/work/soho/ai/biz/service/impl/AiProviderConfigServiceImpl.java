package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.mapper.AiProviderConfigMapper;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.utils.LocalTtlCache;
import work.soho.common.core.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AiProviderConfigServiceImpl extends ServiceImpl<AiProviderConfigMapper, AiProviderConfig>
        implements AiProviderConfigService {
    private static final long LOCAL_CACHE_TTL_MS = 45_000L;
    private static final String ENABLED_PROVIDER_KEY = "enabled_provider_configs";
    private static final String ENABLED_PROVIDER_BY_TYPE_PREFIX = "enabled_provider_configs:";

    private final LocalTtlCache<String, List<AiProviderConfig>> enabledProviderCache = new LocalTtlCache<>(LOCAL_CACHE_TTL_MS);

    @Override
    public List<AiProviderConfig> listEnabledProviderConfigs() {
        return enabledProviderCache.get(ENABLED_PROVIDER_KEY, this::loadEnabledProviderConfigsForCache);
    }

    @Override
    public List<AiProviderConfig> listEnabledProviderConfigsByProvider(String provider) {
        if (StringUtils.isBlank(provider)) {
            return listEnabledProviderConfigs();
        }
        String normalizedProvider = provider.trim();
        return enabledProviderCache.get(ENABLED_PROVIDER_BY_TYPE_PREFIX + normalizedProvider,
                () -> loadEnabledProviderConfigsByProviderForCache(normalizedProvider));
    }

    @Override
    public void clearEnabledProviderCaches() {
        enabledProviderCache.clear();
    }

    protected List<AiProviderConfig> loadEnabledProviderConfigsForCache() {
        return new ArrayList<>(list(new LambdaQueryWrapper<AiProviderConfig>()
                .eq(AiProviderConfig::getStatus, 1)
                .orderByAsc(AiProviderConfig::getId)));
    }

    protected List<AiProviderConfig> loadEnabledProviderConfigsByProviderForCache(String provider) {
        return new ArrayList<>(list(new LambdaQueryWrapper<AiProviderConfig>()
                .eq(AiProviderConfig::getStatus, 1)
                .eq(AiProviderConfig::getProvider, provider)
                .orderByAsc(AiProviderConfig::getId)));
    }
}
