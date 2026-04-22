package work.soho.pay.biz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.StringUtils;
import work.soho.pay.biz.service.PayManualOrderPollNotifier;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis 阻塞队列的支付单长轮询通知器实现。
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class PayManualOrderPollNotifierRedisImpl implements PayManualOrderPollNotifier {
    private static final long QUEUE_EXPIRE_HOURS = 24L;
    private static final String KEY_PREFIX = "pay:custom_qr:poll_queue:";

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 通知指定支付方式有新的支付单创建。
     *
     * @param payInfoId 支付方式 ID
     * @param orderNo 支付单号
     */
    @Override
    public void notifyNewOrder(Integer payInfoId, String orderNo) {
        if (payInfoId == null || StringUtils.isBlank(orderNo)) {
            return;
        }
        String key = buildQueueKey(payInfoId);
        try {
            stringRedisTemplate.opsForList().leftPush(key, orderNo);
            stringRedisTemplate.expire(key, QUEUE_EXPIRE_HOURS, TimeUnit.HOURS);
        } catch (Exception ex) {
            log.warn("写入支付单长轮询通知失败, payInfoId={}, orderNo={}", payInfoId, orderNo, ex);
        }
    }

    /**
     * 阻塞等待指定支付方式的新支付单通知。
     *
     * @param payInfoId 支付方式 ID
     * @param waitSeconds 等待秒数
     * @return 是否收到通知
     */
    @Override
    public boolean awaitNewOrder(Integer payInfoId, int waitSeconds) {
        if (payInfoId == null || waitSeconds <= 0) {
            return false;
        }
        try {
            String value = stringRedisTemplate.opsForList()
                    .rightPop(buildQueueKey(payInfoId), Duration.ofSeconds(waitSeconds));
            return StringUtils.isNotBlank(value);
        } catch (Exception ex) {
            log.warn("等待支付单长轮询通知失败, payInfoId={}, waitSeconds={}", payInfoId, waitSeconds, ex);
            return false;
        }
    }

    /**
     * 构建 Redis 队列 Key。
     *
     * @param payInfoId 支付方式 ID
     * @return 队列 Key
     */
    private String buildQueueKey(Integer payInfoId) {
        return KEY_PREFIX + payInfoId;
    }
}
