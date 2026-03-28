package work.soho.ai.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import work.soho.ai.biz.service.AiMemberRequestLimitService;
import work.soho.ai.biz.service.AiUserMemberCardService;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

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
        if (activeMemberCard == null || !activeMemberCard.isPresent()) {
            return Decision.nonMember();
        }
        AiUserMemberCardService.ActiveMemberCard card = activeMemberCard.get();
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

        if (sevenDayEnabled) {
            long count7d = countInWindow(build7dKey(userId, userCardId), nowMillis, Duration.ofDays(sevenDayWindowDays));
            if (count7d >= sevenDayLimit) {
                return new Decision(true, true, userId, userCardId, nowMillis, sevenDayWindowDays, fiveHourWindowHours, true, fiveHourEnabled);
            }
        }

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
        if (decision == null || !decision.canConsumeQuota()) {
            return;
        }
        long nowMillis = decision.getNowMillis() == null ? Instant.now().toEpochMilli() : decision.getNowMillis();
        String member = nowMillis + ":" + (requestId == null ? "na" : requestId);
        if (decision.isSevenDayEnabled()) {
            addWindowRecord(build7dKey(decision.getUserId(), decision.getCardId()), member, nowMillis,
                    Duration.ofDays(Math.max(1, decision.getSevenDayWindowDays())));
        }
        if (decision.isFiveHourEnabled()) {
            addWindowRecord(build5hKey(decision.getUserId(), decision.getCardId()), member, nowMillis,
                    Duration.ofHours(Math.max(1, decision.getFiveHourWindowHours())));
        }
    }

    private long countInWindow(String key, long nowMillis, Duration duration) {
        long windowStart = nowMillis - duration.toMillis();
        ZSetOperations<String, String> zSet = stringRedisTemplate.opsForZSet();
        zSet.removeRangeByScore(key, Double.NEGATIVE_INFINITY, (double) windowStart);
        Long count = zSet.count(key, (double) windowStart, (double) nowMillis);
        return count == null ? 0L : count;
    }

    private void addWindowRecord(String key, String member, long nowMillis, Duration duration) {
        long windowStart = nowMillis - duration.toMillis();
        ZSetOperations<String, String> zSet = stringRedisTemplate.opsForZSet();
        zSet.removeRangeByScore(key, Double.NEGATIVE_INFINITY, (double) windowStart);
        zSet.add(key, member, (double) nowMillis);
        stringRedisTemplate.expire(key, duration);
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
}
