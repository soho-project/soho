package work.soho.ai.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.soho.ai.biz.domain.AiModelInfo;

import java.util.List;

public interface AiModelInfoService extends IService<AiModelInfo> {
    /**
     * 按模型名查询启用中的模型配置。
     *
     * @param modelName 模型名
     * @return 模型配置
     */
    AiModelInfo findEnabledByModelName(String modelName);

    /**
     * 按主键查询启用中的模型配置。
     *
     * @param id 模型ID
     * @return 模型配置
     */
    AiModelInfo findEnabledById(Long id);

    /**
     * 查询全部启用中的模型配置。
     *
     * @return 模型列表
     */
    List<AiModelInfo> listEnabledModels();
}
