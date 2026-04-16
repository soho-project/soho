package work.soho.ai.biz.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import work.soho.ai.biz.config.AiSysConfig;
import work.soho.ai.biz.domain.AiApiCallLog;
import work.soho.ai.biz.domain.AiUserApiKey;
import work.soho.ai.biz.dto.AiOpenApiGuardContext;
import work.soho.ai.biz.enums.AiUserApiKeyEnums;
import work.soho.ai.biz.exception.AiOpenApiGuardException;
import work.soho.ai.biz.service.AiApiCallLogService;
import work.soho.ai.biz.service.AiUserApiKeyService;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AiOpenApiGuardServiceImplTest {
    private AiUserApiKeyService aiUserApiKeyService;
    private AiApiCallLogService aiApiCallLogService;
    private StringRedisTemplate stringRedisTemplate;
    private ValueOperations<String, String> valueOperations;
    private AiSysConfig aiSysConfig;
    private AiOpenApiGuardServiceImpl service;

    @Before
    public void setUp() {
        aiUserApiKeyService = Mockito.mock(AiUserApiKeyService.class);
        aiApiCallLogService = Mockito.mock(AiApiCallLogService.class);
        stringRedisTemplate = Mockito.mock(StringRedisTemplate.class);
        valueOperations = Mockito.mock(ValueOperations.class);
        aiSysConfig = Mockito.mock(AiSysConfig.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(aiSysConfig.isOpenApiRateLimitEnabled()).thenReturn(false);
        when(aiSysConfig.isOpenApiBanEnabled()).thenReturn(false);
        service = new AiOpenApiGuardServiceImpl(aiUserApiKeyService, aiApiCallLogService, stringRedisTemplate, aiSysConfig);
    }

    @Test
    public void checkAndAcquire_whenApiKeyValid_shouldReturnContext() {
        AiUserApiKey apiKey = enabledApiKey();
        when(aiUserApiKeyService.findByPlaintextKey("token")).thenReturn(apiKey);

        AiOpenApiGuardContext context = service.checkAndAcquire("Bearer token", "/ai/guest/openai/v1/models");

        assertThat(context.getApiKey()).isSameAs(apiKey);
        assertThat(context.getEndpoint()).isEqualTo("/ai/guest/openai/v1/models");
        assertThat(context.getRequestSource()).isEqualTo("guest_openai");
        assertThat(context.getRequestId()).isNotBlank();
        verify(aiApiCallLogService, never()).save(Mockito.any());
    }

    @Test
    public void checkAndAcquire_whenRateLimitExceeded_shouldRejectAndAudit() {
        AiUserApiKey apiKey = enabledApiKey();
        when(aiUserApiKeyService.findByPlaintextKey("token")).thenReturn(apiKey);
        when(aiSysConfig.isOpenApiRateLimitEnabled()).thenReturn(true);
        when(aiSysConfig.getOpenApiRateLimitPerMinute()).thenReturn(1);
        when(aiSysConfig.isOpenApiBanEnabled()).thenReturn(true);
        when(aiSysConfig.getOpenApiBanFailWindowMinutes()).thenReturn(10);
        when(aiSysConfig.getOpenApiBanFailThreshold()).thenReturn(5);
        when(valueOperations.increment(anyString())).thenReturn(2L, 1L);

        assertThatThrownBy(() -> service.checkAndAcquire("Bearer token", "/ai/guest/openai/v1/chat/completions"))
                .isInstanceOf(AiOpenApiGuardException.class)
                .satisfies(ex -> {
                    AiOpenApiGuardException guardException = (AiOpenApiGuardException) ex;
                    assertThat(guardException.getErrorCode()).isEqualTo("rate_limit_exceeded");
                    assertThat(guardException.getHttpStatus()).isEqualTo(429);
                });

        ArgumentCaptor<AiApiCallLog> captor = ArgumentCaptor.forClass(AiApiCallLog.class);
        verify(aiApiCallLogService).save(captor.capture());
        assertThat(captor.getValue().getRejectReason()).isEqualTo("rate_limit");
        assertThat(captor.getValue().getRiskHit()).isEqualTo(1);
    }

    @Test
    public void checkAndAcquire_whenApiKeyBanned_shouldReject() {
        AiUserApiKey apiKey = enabledApiKey();
        when(aiUserApiKeyService.findByPlaintextKey("token")).thenReturn(apiKey);
        when(aiSysConfig.isOpenApiBanEnabled()).thenReturn(true);
        when(stringRedisTemplate.hasKey(anyString())).thenReturn(true);

        assertThatThrownBy(() -> service.checkAndAcquire("Bearer token", "/ai/guest/openai/v1/models"))
                .isInstanceOf(AiOpenApiGuardException.class)
                .satisfies(ex -> {
                    AiOpenApiGuardException guardException = (AiOpenApiGuardException) ex;
                    assertThat(guardException.getErrorCode()).isEqualTo("temporarily_banned");
                    assertThat(guardException.isBanHit()).isTrue();
                });
    }

    @Test
    public void recordFailure_whenRiskFailureReachesThreshold_shouldCreateBan() {
        when(aiSysConfig.isOpenApiBanEnabled()).thenReturn(true);
        when(aiSysConfig.getOpenApiBanFailWindowMinutes()).thenReturn(10);
        when(aiSysConfig.getOpenApiBanFailThreshold()).thenReturn(1);
        when(aiSysConfig.getOpenApiBanDurationMinutes()).thenReturn(30);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        AiOpenApiGuardContext context = new AiOpenApiGuardContext();
        context.setApiKey(enabledApiKey());

        service.recordFailure(context, new IllegalArgumentException("rate limit exceeded"));

        verify(valueOperations).set(anyString(), anyString(), eq(30L), eq(TimeUnit.MINUTES));
    }

    @Test
    public void recordFailure_whenMessageNotRisky_shouldIgnore() {
        AiOpenApiGuardContext context = new AiOpenApiGuardContext();
        context.setApiKey(enabledApiKey());

        service.recordFailure(context, new IllegalArgumentException("socket timeout"));

        verify(valueOperations, never()).increment(anyString());
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), eq(TimeUnit.MINUTES));
    }

    private AiUserApiKey enabledApiKey() {
        AiUserApiKey apiKey = new AiUserApiKey();
        apiKey.setId(11L);
        apiKey.setUserId(7L);
        apiKey.setStatus(AiUserApiKeyEnums.Status.ENABLED.getId());
        return apiKey;
    }
}
