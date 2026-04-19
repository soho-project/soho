package work.soho.ai.biz.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * 轻量本地 TTL 缓存。
 */
public class LocalTtlCache<K, V> {
    private final long ttlMs;
    private final ConcurrentMap<K, CacheValue<V>> store = new ConcurrentHashMap<>();

    public LocalTtlCache(long ttlMs) {
        if (ttlMs <= 0) {
            throw new IllegalArgumentException("ttlMs must be positive");
        }
        this.ttlMs = ttlMs;
    }

    public V get(K key, Supplier<V> loader) {
        long now = System.currentTimeMillis();
        CacheValue<V> cached = store.get(key);
        if (cached != null && !cached.isExpired(now)) {
            return cached.value;
        }
        CacheValue<V> refreshed = store.compute(key, (ignored, existing) -> {
            long current = System.currentTimeMillis();
            if (existing != null && !existing.isExpired(current)) {
                return existing;
            }
            V value = loader.get();
            return new CacheValue<>(value, current + ttlMs);
        });
        return refreshed == null ? null : refreshed.value;
    }

    public void invalidate(K key) {
        if (key != null) {
            store.remove(key);
        }
    }

    public void invalidateAll(Collection<K> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        for (K key : keys) {
            invalidate(key);
        }
    }

    public void clear() {
        store.clear();
    }

    public Map<K, V> snapshot() {
        Map<K, V> result = new ConcurrentHashMap<>();
        long now = System.currentTimeMillis();
        for (Map.Entry<K, CacheValue<V>> entry : store.entrySet()) {
            if (!entry.getValue().isExpired(now)) {
                result.put(entry.getKey(), entry.getValue().value);
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private static final class CacheValue<V> {
        private final V value;
        private final long expireAt;

        private CacheValue(V value, long expireAt) {
            this.value = value;
            this.expireAt = expireAt;
        }

        private boolean isExpired(long now) {
            return now >= expireAt;
        }
    }
}
