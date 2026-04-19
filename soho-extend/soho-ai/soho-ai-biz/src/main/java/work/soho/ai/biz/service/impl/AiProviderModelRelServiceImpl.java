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
import work.soho.ai.biz.utils.LocalTtlCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class AiProviderModelRelServiceImpl extends ServiceImpl<AiProviderModelRelMapper, AiProviderModelRel>
        implements AiProviderModelRelService {
    private static final long DEFAULT_LOCAL_CACHE_TTL_MS = 45_000L;

    private final AiModelInfoService aiModelInfoService;
    private final AtomicLong localCacheTtlMs = new AtomicLong(DEFAULT_LOCAL_CACHE_TTL_MS);
    private final AtomicBoolean localCacheEnabled = new AtomicBoolean(true);

    private LocalTtlCache<Long, List<AiModelInfo>> providerModelsCache = new LocalTtlCache<>(DEFAULT_LOCAL_CACHE_TTL_MS);
    private LocalTtlCache<String, List<Long>> modelProviderIdsCache = new LocalTtlCache<>(DEFAULT_LOCAL_CACHE_TTL_MS);

    @Override
    public List<AiModelInfo> listEnabledModelsByProviderConfigId(Long providerConfigId) {
        if (providerConfigId == null) {
            return Collections.emptyList();
        }
        if (!localCacheEnabled.get()) {
            return loadEnabledModelsByProviderConfigIdForCache(providerConfigId);
        }
        return providerModelsCache.get(providerConfigId, () -> loadEnabledModelsByProviderConfigIdForCache(providerConfigId));
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
        List<Long> providerConfigIds = listEnabledProviderConfigIdsByModelName(modelName);
        return providerConfigIds.isEmpty() ? null : providerConfigIds.get(0);
    }

    @Override
    public List<Long> listEnabledProviderConfigIdsByModelName(String modelName) {
        if (modelName == null || modelName.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String normalizedModelName = modelName.trim();
        if (!localCacheEnabled.get()) {
            return loadEnabledProviderConfigIdsByModelNameForCache(normalizedModelName);
        }
        return modelProviderIdsCache.get(normalizedModelName,
                () -> loadEnabledProviderConfigIdsByModelNameForCache(normalizedModelName));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceRelations(Long providerConfigId, List<Long> modelInfoIds) {
        if (providerConfigId == null) {
            return;
        }
        List<String> oldModelNames = extractModelNames(listEnabledModelsByProviderConfigId(providerConfigId));

        remove(new LambdaQueryWrapper<AiProviderModelRel>()
                .eq(AiProviderModelRel::getProviderConfigId, providerConfigId));

        if (modelInfoIds != null && !modelInfoIds.isEmpty()) {
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

        List<String> newModelNames = extractModelNames(loadEnabledModelsByProviderConfigIdForCache(providerConfigId));
        Set<String> affectedModelNames = new LinkedHashSet<>(oldModelNames);
        affectedModelNames.addAll(newModelNames);
        clearLocalCaches(providerConfigId, new ArrayList<>(affectedModelNames));
    }

    public void clearLocalCaches() {
        providerModelsCache.clear();
        modelProviderIdsCache.clear();
    }

    public void clearLocalCaches(Long providerConfigId, List<String> modelNames) {
        if (providerConfigId != null) {
            providerModelsCache.invalidate(providerConfigId);
        }
        if (modelNames != null && !modelNames.isEmpty()) {
            modelProviderIdsCache.invalidateAll(modelNames);
        }
    }

    public void setLocalCacheTtlMsForTest(Long ttlMs) {
        if (ttlMs == null || ttlMs <= 0) {
            throw new IllegalArgumentException("ttlMs must be positive");
        }
        localCacheTtlMs.set(ttlMs);
        providerModelsCache = new LocalTtlCache<>(ttlMs);
        modelProviderIdsCache = new LocalTtlCache<>(ttlMs);
    }

    public void setLocalCacheEnabledForTest(boolean enabled) {
        localCacheEnabled.set(enabled);
        if (!enabled) {
            clearLocalCaches();
        }
    }

    public void resetLocalCacheSettingsForTest() {
        localCacheEnabled.set(true);
        setLocalCacheTtlMsForTest(DEFAULT_LOCAL_CACHE_TTL_MS);
        clearLocalCaches();
    }

    public int localModelListCacheSizeForTest() {
        return providerModelsCache.snapshot().size();
    }

    public int localProviderIdCacheSizeForTest() {
        return modelProviderIdsCache.snapshot().size();
    }

    public Map<Long, List<AiModelInfo>> localModelCacheSnapshotForTest() {
        return providerModelsCache.snapshot();
    }

    public Map<String, List<Long>> localProviderIdCacheSnapshotForTest() {
        return modelProviderIdsCache.snapshot();
    }

    protected List<AiModelInfo> loadEnabledModelsByProviderConfigIdForCache(Long providerConfigId) {
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

    protected List<Long> loadEnabledProviderConfigIdsByModelNameForCache(String modelName) {
        List<AiModelInfo> modelInfos = aiModelInfoService.list(new LambdaQueryWrapper<AiModelInfo>()
                .eq(AiModelInfo::getModelName, modelName)
                .eq(AiModelInfo::getStatus, 1)
                .orderByAsc(AiModelInfo::getSort)
                .orderByAsc(AiModelInfo::getId));
        if (modelInfos.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> providerConfigIds = new LinkedHashSet<>();
        for (AiModelInfo modelInfo : modelInfos) {
            List<AiProviderModelRel> relList = list(new LambdaQueryWrapper<AiProviderModelRel>()
                    .eq(AiProviderModelRel::getModelInfoId, modelInfo.getId())
                    .eq(AiProviderModelRel::getStatus, 1)
                    .orderByAsc(AiProviderModelRel::getSort)
                    .orderByAsc(AiProviderModelRel::getId));
            for (AiProviderModelRel rel : relList) {
                if (rel.getProviderConfigId() != null) {
                    providerConfigIds.add(rel.getProviderConfigId());
                }
            }
        }
        return new ArrayList<>(providerConfigIds);
    }

    private List<String> extractModelNames(List<AiModelInfo> modelInfos) {
        if (modelInfos == null || modelInfos.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> result = new LinkedHashSet<>();
        for (AiModelInfo modelInfo : modelInfos) {
            if (modelInfo != null && modelInfo.getModelName() != null && !modelInfo.getModelName().trim().isEmpty()) {
                result.add(modelInfo.getModelName().trim());
            }
        }
        return new ArrayList<>(result);
    }
}
