package work.soho.ai.biz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.dto.AiProxyRuntimeStateSnapshot;
import work.soho.ai.biz.service.AiProxyRuntimeStateRepository;
import work.soho.common.core.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis 的 AI 代理节点运行时状态仓储实现。
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class AiProxyRuntimeStateRedisRepositoryImpl implements AiProxyRuntimeStateRepository {
    private static final String KEY_PREFIX = "ai:proxy:state:";
    private static final long TTL_HOURS = 24L;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 保存指定代理节点运行时状态。
     *
     * @param snapshot 运行时状态快照
     */
    @Override
    public void save(AiProxyRuntimeStateSnapshot snapshot) {
        if (snapshot == null || snapshot.getProxyConfigId() == null) {
            return;
        }
        String key = buildKey(snapshot.getProxyConfigId());
        Map<String, String> valueMap = toValueMap(snapshot);
        if (valueMap.isEmpty()) {
            return;
        }
        stringRedisTemplate.opsForHash().putAll(key, valueMap);
        stringRedisTemplate.expire(key, TTL_HOURS, TimeUnit.HOURS);
    }

    /**
     * 读取指定代理节点运行时状态。
     *
     * @param proxyConfigId 代理配置ID
     * @return 运行时状态
     */
    @Override
    public Optional<AiProxyRuntimeStateSnapshot> findById(Long proxyConfigId) {
        if (proxyConfigId == null) {
            return Optional.empty();
        }
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(buildKey(proxyConfigId));
        if (entries == null || entries.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(fromEntries(proxyConfigId, entries));
    }

    /**
     * 批量读取代理节点运行时状态。
     *
     * @param proxyConfigIds 代理配置ID集合
     * @return 状态映射
     */
    @Override
    public Map<Long, AiProxyRuntimeStateSnapshot> findByIds(Collection<Long> proxyConfigIds) {
        if (proxyConfigIds == null || proxyConfigIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, AiProxyRuntimeStateSnapshot> result = new HashMap<>();
        for (Long proxyConfigId : proxyConfigIds) {
            findById(proxyConfigId).ifPresent(snapshot -> result.put(proxyConfigId, snapshot));
        }
        return result;
    }

    /**
     * 删除指定代理节点运行时状态。
     *
     * @param proxyConfigId 代理配置ID
     */
    @Override
    public void delete(Long proxyConfigId) {
        if (proxyConfigId == null) {
            return;
        }
        stringRedisTemplate.delete(buildKey(proxyConfigId));
    }

    /**
     * 组装 Redis key。
     *
     * @param proxyConfigId 代理配置ID
     * @return Redis key
     */
    private String buildKey(Long proxyConfigId) {
        return KEY_PREFIX + proxyConfigId;
    }

    /**
     * 将运行时状态快照转换为 Redis 哈希字段。
     *
     * @param snapshot 运行时状态快照
     * @return Redis 哈希字段
     */
    private Map<String, String> toValueMap(AiProxyRuntimeStateSnapshot snapshot) {
        Map<String, String> valueMap = new HashMap<>();
        putLong(valueMap, "proxyConfigId", snapshot.getProxyConfigId());
        putInt(valueMap, "baseWeight", snapshot.getBaseWeight());
        putInt(valueMap, "effectiveWeight", snapshot.getEffectiveWeight());
        putBoolean(valueMap, "requestAllowed", snapshot.getRequestAllowed());
        putBoolean(valueMap, "circuitOpen", snapshot.getCircuitOpen());
        putLong(valueMap, "circuitOpenUntilMs", snapshot.getCircuitOpenUntilMs());
        putLong(valueMap, "lastSuccessAtMs", snapshot.getLastSuccessAtMs());
        putLong(valueMap, "lastFailureAtMs", snapshot.getLastFailureAtMs());
        putLong(valueMap, "ewmaTotalMs", snapshot.getEwmaTotalMs());
        putInt(valueMap, "consecutiveFailures", snapshot.getConsecutiveFailures());
        putInt(valueMap, "consecutiveSlowRequests", snapshot.getConsecutiveSlowRequests());
        putLong(valueMap, "totalSuccessCount", snapshot.getTotalSuccessCount());
        putLong(valueMap, "totalFailureCount", snapshot.getTotalFailureCount());
        if (StringUtils.isNotBlank(snapshot.getLastErrorMessage())) {
            valueMap.put("lastErrorMessage", snapshot.getLastErrorMessage());
        }
        return valueMap;
    }

    /**
     * 将 Redis 哈希字段转换为运行时状态快照。
     *
     * @param proxyConfigId 代理配置ID
     * @param entries Redis 哈希字段
     * @return 运行时状态快照
     */
    private AiProxyRuntimeStateSnapshot fromEntries(Long proxyConfigId, Map<Object, Object> entries) {
        AiProxyRuntimeStateSnapshot snapshot = new AiProxyRuntimeStateSnapshot();
        snapshot.setProxyConfigId(proxyConfigId);
        snapshot.setBaseWeight(parseInteger(entries.get("baseWeight")));
        snapshot.setEffectiveWeight(parseInteger(entries.get("effectiveWeight")));
        snapshot.setRequestAllowed(parseBoolean(entries.get("requestAllowed")));
        snapshot.setCircuitOpen(parseBoolean(entries.get("circuitOpen")));
        snapshot.setCircuitOpenUntilMs(parseLong(entries.get("circuitOpenUntilMs")));
        snapshot.setLastSuccessAtMs(parseLong(entries.get("lastSuccessAtMs")));
        snapshot.setLastFailureAtMs(parseLong(entries.get("lastFailureAtMs")));
        snapshot.setEwmaTotalMs(parseLong(entries.get("ewmaTotalMs")));
        snapshot.setConsecutiveFailures(parseInteger(entries.get("consecutiveFailures")));
        snapshot.setConsecutiveSlowRequests(parseInteger(entries.get("consecutiveSlowRequests")));
        snapshot.setLastErrorMessage(parseString(entries.get("lastErrorMessage")));
        snapshot.setTotalSuccessCount(parseLong(entries.get("totalSuccessCount")));
        snapshot.setTotalFailureCount(parseLong(entries.get("totalFailureCount")));
        return snapshot;
    }

    /**
     * 在值非空时写入 Long 字段。
     *
     * @param map 目标 map
     * @param key 字段名
     * @param value 字段值
     */
    private void putLong(Map<String, String> map, String key, Long value) {
        if (value != null) {
            map.put(key, String.valueOf(value));
        }
    }

    /**
     * 在值非空时写入 Integer 字段。
     *
     * @param map 目标 map
     * @param key 字段名
     * @param value 字段值
     */
    private void putInt(Map<String, String> map, String key, Integer value) {
        if (value != null) {
            map.put(key, String.valueOf(value));
        }
    }

    /**
     * 在值非空时写入 Boolean 字段。
     *
     * @param map 目标 map
     * @param key 字段名
     * @param value 字段值
     */
    private void putBoolean(Map<String, String> map, String key, Boolean value) {
        if (value != null) {
            map.put(key, String.valueOf(value));
        }
    }

    /**
     * 解析 Long 字段。
     *
     * @param value 原始值
     * @return 解析结果
     */
    private Long parseLong(Object value) {
        String text = parseString(value);
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException ex) {
            log.warn("invalid proxy runtime state long value, value={}", value);
            return null;
        }
    }

    /**
     * 解析 Integer 字段。
     *
     * @param value 原始值
     * @return 解析结果
     */
    private Integer parseInteger(Object value) {
        String text = parseString(value);
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            log.warn("invalid proxy runtime state integer value, value={}", value);
            return null;
        }
    }

    /**
     * 解析 Boolean 字段。
     *
     * @param value 原始值
     * @return 解析结果
     */
    private Boolean parseBoolean(Object value) {
        String text = parseString(value);
        return StringUtils.isBlank(text) ? null : Boolean.parseBoolean(text);
    }

    /**
     * 解析字符串字段。
     *
     * @param value 原始值
     * @return 解析结果
     */
    private String parseString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
