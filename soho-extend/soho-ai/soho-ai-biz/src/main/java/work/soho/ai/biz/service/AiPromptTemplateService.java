package work.soho.ai.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.ai.biz.domain.AiPromptTemplate;

/**
 * 提示词模板服务。
 */
public interface AiPromptTemplateService extends IService<AiPromptTemplate> {
    /**
     * 按模板编码或场景匹配当前生效模板。
     *
     * @param templateCode 模板编码
     * @param sceneCode 场景编码
     * @param providerCode 提供方编码
     * @param model 模型
     * @return 命中的模板
     */
    AiPromptTemplate findActiveTemplate(String templateCode, String sceneCode, String providerCode, String model);
}
