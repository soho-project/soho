package work.soho.ai.biz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.soho.ai.biz.config.AiSysConfig;
import work.soho.ai.biz.domain.AiApiCallLog;
import work.soho.ai.biz.domain.AiUserApiKey;
import work.soho.ai.biz.dto.AiOpenApiGuardContext;
import work.soho.ai.biz.enums.AiApiCallLogEnums;
import work.soho.ai.biz.enums.AiUserApiKeyEnums;
import work.soho.ai.biz.exception.AiOpenApiGuardException;
import work.soho.ai.biz.service.AiApiCallLogService;
import work.soho.ai.biz.service.AiOpenApiGuardService;
import work.soho.ai.biz.service.AiUserApiKeyService;
import work.soho.common.core.util.IDGeneratorUtils;
import work.soho.common.core.util.IpUtils;
import work.soho.common.core.util.RequestUtil;
import work.soho.common.core.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AiOpenApiGuardServiceImpl implements AiOpenApiGuardService {
    private static final String REQUEST_SOURCE = "guest_openai";
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    private final AiUserApiKeyService aiUserApiKeyService;
    private final AiApiCallLogService aiApiCallLogService;
    private final StringRedisTemplate stringRedisTemplate;
    private final AiSysConfig aiSysConfig;

    @Override
    public AiOpenApiGuardContext checkAndAcquire(String authorization, String endpoint) {
        String clientIp = safeClientIp();
        String userAgent = safeHeader(USER_AGENT_HEADER);
        String token = extractBearerToken(authorization);
        AiUserApiKey apiKey = aiUserApiKeyService.findByPlaintextKey(token);
        if (apiKey == null) {
            logReject(null, endpoint, clientIp, userAgent, "invalid_api_key", false, false, "无效的api key");
            throw new AiOpenApiGuardException("invalid api key", "无效的api key", "invalid_api_key",
                    "invalid_api_key", false, false, 401);
        }
        if (apiKey.getStatus() == null
                || apiKey.getStatus().intValue() != AiUserApiKeyEnums.Status.ENABLED.getId()) {
            logReject(apiKey, endpoint, clientIp, userAgent, "disabled_api_key", false, false, "api key已禁用");
            throw new AiOpenApiGuardException("disabled api key", "api key已禁用", "invalid_api_key",
                    "disabled_api_key", false, false, 403);
        }
        if (isTemporarilyBanned(apiKey.getId())) {
            logReject(apiKey, endpoint, clientIp, userAgent, "temporary_ban", true, true, "api key已被临时封禁");
            throw new AiOpenApiGuardException("api key temporarily banned", "api key已被临时封禁", "temporarily_banned",
                    "temporary_ban", true, true, 403);
        }
        if (isRateLimitExceeded(apiKey.getId())) {
            boolean banned = recordRiskFailure(apiKey.getId());
            logReject(apiKey, endpoint, clientIp, userAgent, "rate_limit", true, banned, "请求过于频繁，请稍后再试");
            throw new AiOpenApiGuardException("rate limit exceeded", "请求过于频繁，请稍后再试", "rate_limit_exceeded",
                    "rate_limit", true, banned, 429);
        }

        AiOpenApiGuardContext context = new AiOpenApiGuardContext();
        context.setRequestId(IDGeneratorUtils.uuid32());
        context.setEndpoint(endpoint);
        context.setRequestSource(REQUEST_SOURCE);
        context.setClientIp(clientIp);
        context.setUserAgent(userAgent);
        context.setApiKey(apiKey);
        return context;
    }

    @Override
    public void recordFailure(AiOpenApiGuardContext context, Throwable throwable) {
        if (context == null || context.getApiKey() == null || throwable == null) {
            return;
        }
        if (!shouldCountAsRiskFailure(throwable)) {
            return;
        }
        recordRiskFailure(context.getApiKey().getId());
    }

    private String extractBearerToken(String authorization) {
        Assert.hasText(authorization, "Authorization不能为空");
        Assert.isTrue(authorization.startsWith(AUTHORIZATION_PREFIX), "Authorization格式错误");
        String token = authorization.substring(AUTHORIZATION_PREFIX.length()).trim();
        Assert.hasText(token, "api key不能为空");
        return token;
    }

    private boolean isTemporarilyBanned(Long apiKeyId) {
        if (!aiSysConfig.isOpenApiBanEnabled()) {
            return false;
        }
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(buildBanKey(apiKeyId)));
    }

    private boolean isRateLimitExceeded(Long apiKeyId) {
        if (!aiSysConfig.isOpenApiRateLimitEnabled()) {
            return false;
        }
        int limit = Math.max(1, aiSysConfig.getOpenApiRateLimitPerMinute());
        long nowMillis = System.currentTimeMillis();
        String key = buildRateLimitKey(apiKeyId, nowMillis);
        Long current = stringRedisTemplate.opsForValue().increment(key);
        if (current == null) {
            return false;
        }
        if (current == 1L) {
            stringRedisTemplate.expire(key, computeCurrentBucketTtlMillis(nowMillis, TimeUnit.MINUTES.toMillis(1)),
                    TimeUnit.MILLISECONDS);
        }
        return current > limit;
    }

    private boolean recordRiskFailure(Long apiKeyId) {
        if (apiKeyId == null || !aiSysConfig.isOpenApiBanEnabled()) {
            return false;
        }
        long nowMillis = System.currentTimeMillis();
        long windowMillis = TimeUnit.MINUTES.toMillis(Math.max(1, aiSysConfig.getOpenApiBanFailWindowMinutes()));
        String key = buildFailWindowKey(apiKeyId, nowMillis, windowMillis);
        Long current = stringRedisTemplate.opsForValue().increment(key);
        if (current == null) {
            return false;
        }
        if (current == 1L) {
            stringRedisTemplate.expire(key, computeCurrentBucketTtlMillis(nowMillis, windowMillis), TimeUnit.MILLISECONDS);
        }
        if (current >= Math.max(1, aiSysConfig.getOpenApiBanFailThreshold())) {
            stringRedisTemplate.opsForValue().set(buildBanKey(apiKeyId), String.valueOf(nowMillis),
                    Math.max(1, aiSysConfig.getOpenApiBanDurationMinutes()), TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    private boolean shouldCountAsRiskFailure(Throwable throwable) {
        String message = throwable.getMessage();
        if (StringUtils.isBlank(message)) {
            return false;
        }
        String lower = message.toLowerCase(Locale.ROOT);
        return lower.contains("api key")
                || lower.contains("authorization")
                || lower.contains("invalid_api_key")
                || lower.contains("insufficient")
                || lower.contains("quota")
                || lower.contains("rate limit")
                || lower.contains("too many requests")
                || lower.contains("forbidden")
                || lower.contains("unauthorized");
    }

    private void logReject(AiUserApiKey apiKey,
                           String endpoint,
                           String clientIp,
                           String userAgent,
                           String rejectReason,
                           boolean riskHit,
                           boolean banHit,
                           String errorMessage) {
        AiApiCallLog log = new AiApiCallLog();
        log.setRequestId(IDGeneratorUtils.uuid32());
        if (apiKey != null) {
            log.setUserId(apiKey.getUserId());
            log.setApiKeyId(apiKey.getId());
        }
        log.setEndpoint(endpoint);
        log.setAmount(BigDecimal.ZERO);
        log.setStatus(AiApiCallLogEnums.Status.FAILED.getId());
        log.setErrorMessage(errorMessage);
        log.setClientIp(clientIp);
        log.setUserAgent(userAgent);
        log.setRequestSource(REQUEST_SOURCE);
        log.setRejectReason(rejectReason);
        log.setRiskHit(riskHit ? 1 : 0);
        log.setBanHit(banHit ? 1 : 0);
        log.setCreatedTime(LocalDateTime.now());
        log.setUpdatedTime(LocalDateTime.now());
        aiApiCallLogService.save(log);
    }

    private long computeCurrentBucketTtlMillis(long nowMillis, long windowMillis) {
        long bucketEnd = computeCurrentBucketEndMillis(nowMillis, windowMillis);
        return Math.max(1000L, bucketEnd - nowMillis + 1000L);
    }

    private long computeCurrentBucketEndMillis(long nowMillis, long windowMillis) {
        long safeWindowMillis = Math.max(1L, windowMillis);
        long bucketStart = (nowMillis / safeWindowMillis) * safeWindowMillis;
        return bucketStart + safeWindowMillis;
    }

    private String buildRateLimitKey(Long apiKeyId, long nowMillis) {
        long bucket = nowMillis / TimeUnit.MINUTES.toMillis(1);
        return "rate:ai:openapi:key:" + apiKeyId + ":bucket:" + bucket;
    }

    private String buildFailWindowKey(Long apiKeyId, long nowMillis, long windowMillis) {
        long bucket = nowMillis / Math.max(1L, windowMillis);
        return "risk:ai:openapi:fail:key:" + apiKeyId + ":bucket:" + bucket;
    }

    private String buildBanKey(Long apiKeyId) {
        return "risk:ai:openapi:ban:key:" + apiKeyId;
    }

    private String safeClientIp() {
        try {
            return IpUtils.getClientIp();
        } catch (Exception ex) {
            return null;
        }
    }

    private String safeHeader(String headerName) {
        try {
            return RequestUtil.getHeader(headerName);
        } catch (Exception ex) {
            return null;
        }
    }
}
