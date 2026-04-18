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
    private static final long ANONYMOUS_USER_ID = 0L;
    private static final long ANONYMOUS_API_KEY_ID = 0L;
    private static final long UNKNOWN_PROVIDER_CONFIG_ID = 0L;
    private static final String REQUEST_SOURCE = "guest_openai";
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    private final AiUserApiKeyService aiUserApiKeyService;
    private final AiApiCallLogService aiApiCallLogService;
    private final StringRedisTemplate stringRedisTemplate;
    private final AiSysConfig aiSysConfig;

    /**
     * 校验来访 API Key 是否存在、启用且未过期，并在拒绝时记录审计日志。
     */
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
        try {
            apiKey = aiUserApiKeyService.requireByPlaintextKey(token);
        } catch (IllegalArgumentException ex) {
            if ("api key已禁用".equals(ex.getMessage())) {
                logReject(apiKey, endpoint, clientIp, userAgent, "disabled_api_key", false, false, "api key已禁用");
                throw new AiOpenApiGuardException("disabled api key", "api key已禁用", "invalid_api_key",
                        "disabled_api_key", false, false, 403);
            }
            if ("api key已过期".equals(ex.getMessage())) {
                logReject(apiKey, endpoint, clientIp, userAgent, "expired_api_key", false, false, "api key已过期");
                throw new AiOpenApiGuardException("expired api key", "api key已过期", "invalid_api_key",
                        "expired_api_key", false, false, 403);
            }
            throw ex;
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

    /**
     * 从 Authorization 头中提取 Bearer token。
     */
    private String extractBearerToken(String authorization) {
        Assert.hasText(authorization, "Authorization不能为空");
        Assert.isTrue(authorization.startsWith(AUTHORIZATION_PREFIX), "Authorization格式错误");
        String token = authorization.substring(AUTHORIZATION_PREFIX.length()).trim();
        Assert.hasText(token, "api key不能为空");
        return token;
    }

    /**
     * 判断当前 key 是否已被临时封禁。
     */
    private boolean isTemporarilyBanned(Long apiKeyId) {
        if (!aiSysConfig.isOpenApiBanEnabled()) {
            return false;
        }
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(buildBanKey(apiKeyId)));
    }

    /**
     * 判断当前 key 是否触发分钟级限流。
     */
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

    /**
     * 记录风险失败次数，并在达到阈值时施加临时封禁。
     */
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

    /**
     * 判断异常是否应计入风险失败统计。
     */
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

    /**
     * 记录被守卫层拒绝的请求日志，确保审计表必填字段完整。
     */
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
        log.setUserId(resolveLogUserId(apiKey));
        log.setApiKeyId(resolveLogApiKeyId(apiKey));
        log.setProviderConfigId(resolveLogProviderConfigId(apiKey));
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

    /**
     * 解析日志记录使用的用户ID，匿名或非法 key 请求统一落到占位用户ID，避免日志落库失败。
     */
    private Long resolveLogUserId(AiUserApiKey apiKey) {
        if (apiKey == null || apiKey.getUserId() == null) {
            return ANONYMOUS_USER_ID;
        }
        return apiKey.getUserId();
    }

    /**
     * 解析日志记录使用的 API Key ID，匿名或非法 key 请求统一落到占位 key ID。
     */
    private Long resolveLogApiKeyId(AiUserApiKey apiKey) {
        if (apiKey == null || apiKey.getId() == null) {
            return ANONYMOUS_API_KEY_ID;
        }
        return apiKey.getId();
    }

    /**
     * 解析日志记录使用的 provider 配置ID，访客拦截场景统一使用占位值。
     */
    private Long resolveLogProviderConfigId(AiUserApiKey apiKey) {
        return UNKNOWN_PROVIDER_CONFIG_ID;
    }

    /**
     * 计算当前时间桶剩余 TTL，确保窗口过期时间覆盖到当前桶结束。
     */
    private long computeCurrentBucketTtlMillis(long nowMillis, long windowMillis) {
        long bucketEnd = computeCurrentBucketEndMillis(nowMillis, windowMillis);
        return Math.max(1000L, bucketEnd - nowMillis + 1000L);
    }

    /**
     * 计算当前时间所在桶的结束时间。
     */
    private long computeCurrentBucketEndMillis(long nowMillis, long windowMillis) {
        long safeWindowMillis = Math.max(1L, windowMillis);
        long bucketStart = (nowMillis / safeWindowMillis) * safeWindowMillis;
        return bucketStart + safeWindowMillis;
    }

    /**
     * 构建分钟级限流 Redis Key。
     */
    private String buildRateLimitKey(Long apiKeyId, long nowMillis) {
        long bucket = nowMillis / TimeUnit.MINUTES.toMillis(1);
        return "rate:ai:openapi:key:" + apiKeyId + ":bucket:" + bucket;
    }

    /**
     * 构建风险失败统计 Redis Key。
     */
    private String buildFailWindowKey(Long apiKeyId, long nowMillis, long windowMillis) {
        long bucket = nowMillis / Math.max(1L, windowMillis);
        return "risk:ai:openapi:fail:key:" + apiKeyId + ":bucket:" + bucket;
    }

    /**
     * 构建临时封禁 Redis Key。
     */
    private String buildBanKey(Long apiKeyId) {
        return "risk:ai:openapi:ban:key:" + apiKeyId;
    }

    /**
     * 安全获取客户端 IP。
     */
    private String safeClientIp() {
        try {
            return IpUtils.getClientIp();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 安全获取指定请求头。
     */
    private String safeHeader(String headerName) {
        try {
            return RequestUtil.getHeader(headerName);
        } catch (Exception ex) {
            return null;
        }
    }
}
