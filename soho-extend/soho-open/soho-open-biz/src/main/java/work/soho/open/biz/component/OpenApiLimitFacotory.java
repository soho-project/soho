package work.soho.open.biz.component;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class OpenApiLimitFacotory {
    private static final long IDLE_TIMEOUT_MS = 60_000;

    private final Map<Long, OpenApiLimitWrapper> handlerMap = new ConcurrentHashMap<>();

    /**
     * 获取访问控制处理器
     *
     * @param id
     * @return
     */
    public OpenApiLimitWrapper getHandler(Long id, int max) {
        OpenApiLimitWrapper wrapper = handlerMap.get(id);
        if (wrapper == null || wrapper.getMax() != max) {
            handlerMap.put(id, new OpenApiLimitWrapper(max, 1, TimeUnit.MINUTES));
            wrapper = handlerMap.get(id);
        }
        wrapper.touch();
        return wrapper;
    }

    /**
     * 定时清理超过 1 分钟未访问的 handler
     */
    @Scheduled(fixedDelay = 30_000)
    public void cleanIdleHandlers() {
        long now = System.currentTimeMillis();

        handlerMap.entrySet().removeIf(entry -> {
            OpenApiLimitWrapper wrapper = entry.getValue();
            if (now - wrapper.lastAccessTime > IDLE_TIMEOUT_MS) {
                try {
                    wrapper.destroy();
                    handlerMap.remove(entry.getKey());
                } catch (Exception e) {
                    // 记录日志
                }
                return true;
            }
            return false;
        });
    }
}
