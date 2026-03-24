package work.soho.ai.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.domain.AiProviderModelRel;
import work.soho.ai.biz.mapper.AiProviderModelRelMapper;
import work.soho.ai.biz.service.AiModelInfoService;
import work.soho.ai.biz.service.AiProviderModelRelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AiProviderModelRelServiceImpl extends ServiceImpl<AiProviderModelRelMapper, AiProviderModelRel>
        implements AiProviderModelRelService {
    private final AiModelInfoService aiModelInfoService;

    @Override
    public List<AiModelInfo> listEnabledModelsByProviderConfigId(Long providerConfigId) {
        if (providerConfigId == null) {
            return Collections.emptyList();
        }
        List<AiProviderModelRel> relList = list(new LambdaQueryWrapper<AiProviderModelRel>()
                .eq(AiProviderModelRel::getProviderConfigId, providerConfigId)
                .eq(AiProviderModelRel::getStatus, 1)
                .orderByAsc(AiProviderModelRel::getSort)
                .orderByAsc(AiProviderModelRel::getId));
        if (relList.isEmpty()) {
            return Collections.emptyList();
        }

        List<AiModelInfo> result = new ArrayList<>();
        for (AiProviderModelRel rel : relList) {
            AiModelInfo aiModelInfo = aiModelInfoService.getById(rel.getModelInfoId());
            if (aiModelInfo != null && Integer.valueOf(1).equals(aiModelInfo.getStatus())) {
                result.add(aiModelInfo);
            }
        }
        return result;
    }

    @Override
    public List<Long> listEnabledModelIdsByProviderConfigId(Long providerConfigId) {
        if (providerConfigId == null) {
            return Collections.emptyList();
        }
        List<AiProviderModelRel> relList = list(new LambdaQueryWrapper<AiProviderModelRel>()
                .eq(AiProviderModelRel::getProviderConfigId, providerConfigId)
                .eq(AiProviderModelRel::getStatus, 1)
                .orderByAsc(AiProviderModelRel::getSort)
                .orderByAsc(AiProviderModelRel::getId));
        if (relList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = new ArrayList<>();
        for (AiProviderModelRel rel : relList) {
            if (rel.getModelInfoId() != null) {
                ids.add(rel.getModelInfoId());
            }
        }
        return ids;
    }

    @Override
    public Long findFirstEnabledProviderConfigIdByModelName(String modelName) {
        if (modelName == null || modelName.trim().isEmpty()) {
            return null;
        }
        List<AiModelInfo> modelInfos = aiModelInfoService.list(new LambdaQueryWrapper<AiModelInfo>()
                .eq(AiModelInfo::getModelName, modelName)
                .eq(AiModelInfo::getStatus, 1)
                .orderByAsc(AiModelInfo::getSort)
                .orderByAsc(AiModelInfo::getId));
        if (modelInfos.isEmpty()) {
            return null;
        }
        for (AiModelInfo modelInfo : modelInfos) {
            AiProviderModelRel rel = getOne(new LambdaQueryWrapper<AiProviderModelRel>()
                    .eq(AiProviderModelRel::getModelInfoId, modelInfo.getId())
                    .eq(AiProviderModelRel::getStatus, 1)
                    .orderByAsc(AiProviderModelRel::getSort)
                    .orderByAsc(AiProviderModelRel::getId)
                    .last("limit 1"));
            if (rel != null && rel.getProviderConfigId() != null) {
                return rel.getProviderConfigId();
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceRelations(Long providerConfigId, List<Long> modelInfoIds) {
        if (providerConfigId == null) {
            return;
        }
        remove(new LambdaQueryWrapper<AiProviderModelRel>()
                .eq(AiProviderModelRel::getProviderConfigId, providerConfigId));

        if (modelInfoIds == null || modelInfoIds.isEmpty()) {
            return;
        }

        Set<Long> uniqueIds = new HashSet<>();
        List<AiProviderModelRel> relList = new ArrayList<>();
        int sort = 0;
        for (Long modelInfoId : modelInfoIds) {
            if (modelInfoId == null || !uniqueIds.add(modelInfoId)) {
                continue;
            }
            AiProviderModelRel rel = new AiProviderModelRel();
            rel.setProviderConfigId(providerConfigId);
            rel.setModelInfoId(modelInfoId);
            rel.setStatus(1);
            rel.setSort(sort++);
            relList.add(rel);
        }
        if (!relList.isEmpty()) {
            saveBatch(relList);
        }
    }
}
