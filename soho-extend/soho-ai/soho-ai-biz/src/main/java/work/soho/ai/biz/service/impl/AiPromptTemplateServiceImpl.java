package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiPromptTemplate;
import work.soho.ai.biz.mapper.AiPromptTemplateMapper;
import work.soho.ai.biz.service.AiPromptTemplateService;
import work.soho.common.core.util.StringUtils;

import java.util.Comparator;
import java.util.List;

/**
 * 提示词模板服务实现。
 */
@Service
public class AiPromptTemplateServiceImpl extends ServiceImpl<AiPromptTemplateMapper, AiPromptTemplate>
        implements AiPromptTemplateService {

    /**
     * 按模板编码或场景匹配当前生效模板。
     *
     * @param templateCode 模板编码
     * @param sceneCode 场景编码
     * @param providerCode 提供方编码
     * @param model 模型
     * @return 命中的模板
     */
    @Override
    public AiPromptTemplate findActiveTemplate(String templateCode, String sceneCode, String providerCode, String model) {
        if (StringUtils.isBlank(templateCode) && StringUtils.isBlank(sceneCode)) {
            return null;
        }
        LambdaQueryWrapper<AiPromptTemplate> queryWrapper = new LambdaQueryWrapper<AiPromptTemplate>()
                .eq(AiPromptTemplate::getStatus, 1)
                .orderByDesc(AiPromptTemplate::getVersion)
                .orderByDesc(AiPromptTemplate::getId);
        if (StringUtils.isNotBlank(templateCode)) {
            queryWrapper.eq(AiPromptTemplate::getCode, templateCode);
            return getOne(queryWrapper.last("limit 1"));
        }
        queryWrapper.eq(AiPromptTemplate::getSceneCode, sceneCode);
        List<AiPromptTemplate> templates = list(queryWrapper);
        return templates.stream()
                .filter(item -> matchesProvider(item, providerCode))
                .filter(item -> matchesModel(item, model))
                .max(Comparator
                        .comparingInt((AiPromptTemplate item) -> specificityScore(item, providerCode, model))
                        .thenComparing(AiPromptTemplate::getVersion, Comparator.nullsFirst(Integer::compareTo))
                        .thenComparing(AiPromptTemplate::getId, Comparator.nullsFirst(Long::compareTo)))
                .orElse(null);
    }

    /**
     * 判断模板是否匹配提供方。
     *
     * @param template 模板
     * @param providerCode 提供方编码
     * @return 是否匹配
     */
    private boolean matchesProvider(AiPromptTemplate template, String providerCode) {
        return StringUtils.isBlank(template.getProviderCode())
                || (StringUtils.isNotBlank(providerCode) && template.getProviderCode().equalsIgnoreCase(providerCode));
    }

    /**
     * 判断模板是否匹配模型。
     *
     * @param template 模板
     * @param model 模型
     * @return 是否匹配
     */
    private boolean matchesModel(AiPromptTemplate template, String model) {
        if (StringUtils.isBlank(template.getModelPattern())) {
            return true;
        }
        if (StringUtils.isBlank(model)) {
            return false;
        }
        String pattern = template.getModelPattern().trim();
        if (pattern.contains("*")) {
            String regex = pattern.replace(".", "\\.").replace("*", ".*");
            return model.matches(regex);
        }
        return pattern.equalsIgnoreCase(model);
    }

    /**
     * 计算模板匹配优先级。
     *
     * @param template 模板
     * @param providerCode 提供方编码
     * @param model 模型
     * @return 优先级分值
     */
    private int specificityScore(AiPromptTemplate template, String providerCode, String model) {
        int score = 0;
        if (StringUtils.isNotBlank(template.getProviderCode())
                && StringUtils.isNotBlank(providerCode)
                && template.getProviderCode().equalsIgnoreCase(providerCode)) {
            score += 10;
        }
        if (StringUtils.isNotBlank(template.getModelPattern()) && matchesModel(template, model)) {
            score += 5;
        }
        return score;
    }
}
