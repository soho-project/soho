package work.soho.ai.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.service.AiMemberRequestLimitService;
import work.soho.ai.biz.service.AiUserMemberCardService;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AiMemberRequestLimitServiceImpl implements AiMemberRequestLimitService {
    private static final int DEFAULT_RATE_LIMIT_5H = 100;
    private static final int DEFAULT_RATE_LIMIT_7D = 300;
    private static final int DEFAULT_WINDOW_5H = 5;
    private static final int DEFAULT_WINDOW_7D = 7;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Decision evaluate(Long userId, Optional<AiUserMemberCardService.ActiveMemberCard> activeMemberCard) {
        // 没有生效会员卡，按非会员处理
        if (activeMemberCard == null || !activeMemberCard.isPresent()) {
            return Decision.nonMember();
        }
        AiUserMemberCardService.ActiveMemberCard card = activeMemberCard.get();
        // 当前只实现 by_request 免费配额；其他模式（如 by_token）按非会员处理
        if (!"by_request".equalsIgnoreCase(safeString(card.getLimitMode()))) {
            return Decision.nonMember();
        }
        Long userCardId = card.getUserCardId();
        if (userId == null || userCardId == null) {
            return Decision.nonMember();
        }

        boolean sevenDayEnabled = card.getRateLimit7dEnabled() == null || card.getRateLimit7dEnabled();
        boolean fiveHourEnabled = card.getRateLimit5hEnabled() == null || card.getRateLimit5hEnabled();
        int sevenDayWindowDays = Math.max(1, card.getRateLimitWindow7d() == null ? DEFAULT_WINDOW_7D : card.getRateLimitWindow7d());
        int fiveHourWindowHours = Math.max(1, card.getRateLimitWindow5h() == null ? DEFAULT_WINDOW_5H : card.getRateLimitWindow5h());
        int sevenDayLimit = Math.max(1, card.getRateLimit7d() == null ? DEFAULT_RATE_LIMIT_7D : card.getRateLimit7d());
        int fiveHourLimit = Math.max(1, card.getRateLimit5h() == null ? DEFAULT_RATE_LIMIT_5H : card.getRateLimit5h());
        long nowMillis = Instant.now().toEpochMilli();

        // 先校验长窗口（7天）
        if (sevenDayEnabled) {
            long count7d = countInWindow(build7dKey(userId, userCardId), nowMillis, Duration.ofDays(sevenDayWindowDays));
            if (count7d >= sevenDayLimit) {
                return new Decision(true, true, userId, userCardId, nowMillis, sevenDayWindowDays, fiveHourWindowHours, true, fiveHourEnabled);
            }
        }

        // 再校验短窗口（5小时）
        if (fiveHourEnabled) {
            long count5h = countInWindow(build5hKey(userId, userCardId), nowMillis, Duration.ofHours(fiveHourWindowHours));
            if (count5h >= fiveHourLimit) {
                return new Decision(true, true, userId, userCardId, nowMillis, sevenDayWindowDays, fiveHourWindowHours, sevenDayEnabled, true);
            }
        }

        return new Decision(true, false, userId, userCardId, nowMillis, sevenDayWindowDays, fiveHourWindowHours, sevenDayEnabled, fiveHourEnabled);
    }

    @Override
    public void consumeIfNeeded(Decision decision, String requestId) {
        // 仅在“会员按请求计费且未超限”时，才记录一次配额消耗
        if (decision == null || !decision.canConsumeQuota()) {
            return;
        }
        long nowMillis = decision.getNowMillis() == null ? Instant.now().toEpochMilli() : decision.getNowMillis();
        if (decision.isSevenDayEnabled()) {
            addWindowRecord(build7dKey(decision.getUserId(), decision.getCardId()), nowMillis,
                    Duration.ofDays(Math.max(1, decision.getSevenDayWindowDays())));
        }
        if (decision.isFiveHourEnabled()) {
            addWindowRecord(build5hKey(decision.getUserId(), decision.getCardId()), nowMillis,
                    Duration.ofHours(Math.max(1, decision.getFiveHourWindowHours())));
        }
    }

    @Override
    public UsageSnapshot queryUsage(Long userId, AiUserMemberCardService.ActiveMemberCard activeMemberCard) {
        if (activeMemberCard == null) {
            return UsageSnapshot.empty();
        }
        if (!"by_request".equalsIgnoreCase(safeString(activeMemberCard.getLimitMode()))) {
            return UsageSnapshot.empty();
        }
        Long userCardId = activeMemberCard.getUserCardId();
        if (userId == null || userCardId == null) {
            return UsageSnapshot.empty();
        }

        boolean sevenDayEnabled = activeMemberCard.getRateLimit7dEnabled() == null || activeMemberCard.getRateLimit7dEnabled();
        boolean fiveHourEnabled = activeMemberCard.getRateLimit5hEnabled() == null || activeMemberCard.getRateLimit5hEnabled();
        int sevenDayWindowDays = Math.max(1, activeMemberCard.getRateLimitWindow7d() == null ? DEFAULT_WINDOW_7D : activeMemberCard.getRateLimitWindow7d());
        int fiveHourWindowHours = Math.max(1, activeMemberCard.getRateLimitWindow5h() == null ? DEFAULT_WINDOW_5H : activeMemberCard.getRateLimitWindow5h());
        int sevenDayLimit = Math.max(1, activeMemberCard.getRateLimit7d() == null ? DEFAULT_RATE_LIMIT_7D : activeMemberCard.getRateLimit7d());
        int fiveHourLimit = Math.max(1, activeMemberCard.getRateLimit5h() == null ? DEFAULT_RATE_LIMIT_5H : activeMemberCard.getRateLimit5h());

        long nowMillis = Instant.now().toEpochMilli();
        int sevenDayUsed = sevenDayEnabled
                ? safeToInt(countInWindow(build7dKey(userId, userCardId), nowMillis, Duration.ofDays(sevenDayWindowDays)))
                : 0;
        int fiveHourUsed = fiveHourEnabled
                ? safeToInt(countInWindow(build5hKey(userId, userCardId), nowMillis, Duration.ofHours(fiveHourWindowHours)))
                : 0;
        long sevenDayNextResetMillis = sevenDayEnabled
                ? computeCurrentBucketEndMillis(nowMillis, Duration.ofDays(sevenDayWindowDays))
                : 0L;
        long fiveHourNextResetMillis = fiveHourEnabled
                ? computeCurrentBucketEndMillis(nowMillis, Duration.ofHours(fiveHourWindowHours))
                : 0L;

        return new UsageSnapshot(
                true,
                sevenDayEnabled,
                fiveHourEnabled,
                sevenDayWindowDays,
                fiveHourWindowHours,
                sevenDayLimit,
                fiveHourLimit,
                sevenDayUsed,
                fiveHourUsed,
                sevenDayNextResetMillis,
                fiveHourNextResetMillis
        );
    }

    private long countInWindow(String keyPrefix, long nowMillis, Duration duration) {
        String key = buildFixedWindowKey(keyPrefix, nowMillis, duration);
        String count = stringRedisTemplate.opsForValue().get(key);
        if (count == null) {
            return 0L;
        }
        try {
            return Long.parseLong(count);
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    private void addWindowRecord(String keyPrefix, long nowMillis, Duration duration) {
        String key = buildFixedWindowKey(keyPrefix, nowMillis, duration);
        Long current = stringRedisTemplate.opsForValue().increment(key);
        if (current == null) {
            return;
        }
        if (current == 1L) {
            long ttlMillis = computeCurrentBucketTtlMillis(nowMillis, duration);
            stringRedisTemplate.expire(key, ttlMillis, TimeUnit.MILLISECONDS);
        }
    }

    private String buildFixedWindowKey(String keyPrefix, long nowMillis, Duration duration) {
        long windowMillis = Math.max(1L, duration.toMillis());
        long bucketIndex = nowMillis / windowMillis;
        return keyPrefix + ":bucket:" + bucketIndex;
    }

    private long computeCurrentBucketTtlMillis(long nowMillis, Duration duration) {
        long bucketEnd = computeCurrentBucketEndMillis(nowMillis, duration);
        // 额外保留 1 秒，避免边界时刻误删
        return Math.max(1000L, bucketEnd - nowMillis + 1000L);
    }

    private long computeCurrentBucketEndMillis(long nowMillis, Duration duration) {
        long windowMillis = Math.max(1L, duration.toMillis());
        long bucketStart = (nowMillis / windowMillis) * windowMillis;
        return bucketStart + windowMillis;
    }

    private String build7dKey(Long userId, Long cardId) {
        return "rate:ai:member:7d:user:" + userId + ":userCard:" + cardId;
    }

    private String build5hKey(Long userId, Long cardId) {
        return "rate:ai:member:5h:user:" + userId + ":userCard:" + cardId;
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private int safeToInt(long value) {
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }
}
