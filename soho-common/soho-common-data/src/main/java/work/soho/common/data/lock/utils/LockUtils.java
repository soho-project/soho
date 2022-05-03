package work.soho.common.data.lock.utils;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 获取锁客户端
 */
@Component
public class LockUtils {
    @Autowired
    @Lazy
    private RedissonClient redissonClient;


    private static LockUtils lockUtils;

    @PostConstruct
    public void init() {
        lockUtils = this;
    }

    /**
     * 获取锁客户端
     *
     * @return
     */
    public static RedissonClient getLockClient() {
        return lockUtils.redissonClient;
    }
}
