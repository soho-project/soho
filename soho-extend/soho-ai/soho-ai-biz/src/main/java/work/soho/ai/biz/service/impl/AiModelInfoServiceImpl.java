package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.mapper.AiModelInfoMapper;
import work.soho.ai.biz.service.AiModelInfoService;
import work.soho.common.core.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
public class AiModelInfoServiceImpl extends ServiceImpl<AiModelInfoMapper, AiModelInfo>
        implements AiModelInfoService {
    /**
     * 按模型名查询启用中的模型配置。
     *
     * @param modelName 模型名
     * @return 模型配置
     */
    @Override
    public AiModelInfo findEnabledByModelName(String modelName) {
        if (StringUtils.isBlank(modelName)) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<AiModelInfo>()
                .eq(AiModelInfo::getModelName, modelName.trim())
                .eq(AiModelInfo::getStatus, 1)
                .last("limit 1"));
    }

    /**
     * 按主键查询启用中的模型配置。
     *
     * @param id 模型ID
     * @return 模型配置
     */
    @Override
    public AiModelInfo findEnabledById(Long id) {
        if (id == null) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<AiModelInfo>()
                .eq(AiModelInfo::getId, id)
                .eq(AiModelInfo::getStatus, 1)
                .last("limit 1"));
    }

    /**
     * 查询全部启用中的模型配置。
     *
     * @return 模型列表
     */
    @Override
    public List<AiModelInfo> listEnabledModels() {
        List<AiModelInfo> list = list(new LambdaQueryWrapper<AiModelInfo>()
                .eq(AiModelInfo::getStatus, 1)
                .orderByAsc(AiModelInfo::getSort)
                .orderByAsc(AiModelInfo::getId));
        return list == null ? Collections.emptyList() : list;
    }
}
