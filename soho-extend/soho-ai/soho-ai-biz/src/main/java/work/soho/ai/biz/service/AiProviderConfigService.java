package work.soho.ai.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.ai.biz.domain.AiProviderConfig;

import java.util.List;

public interface AiProviderConfigService extends IService<AiProviderConfig> {
    List<AiProviderConfig> listEnabledProviderConfigs();

    List<AiProviderConfig> listEnabledProviderConfigsByProvider(String provider);

    void clearEnabledProviderCaches();
}
