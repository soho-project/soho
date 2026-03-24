package work.soho.ai.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.domain.AiProviderModelRel;

import java.util.List;

public interface AiProviderModelRelService extends IService<AiProviderModelRel> {
    List<AiModelInfo> listEnabledModelsByProviderConfigId(Long providerConfigId);

    List<Long> listEnabledModelIdsByProviderConfigId(Long providerConfigId);

    Long findFirstEnabledProviderConfigIdByModelName(String modelName);

    void replaceRelations(Long providerConfigId, List<Long> modelInfoIds);
}
