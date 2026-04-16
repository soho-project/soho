package work.soho.ai.biz.service.impl;

import org.junit.Test;
import org.mockito.Mockito;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.ai.biz.domain.AiUserApiKey;
import work.soho.ai.biz.domain.AiModelInfo;
import work.soho.ai.biz.dto.AiChatResponse;
import work.soho.ai.biz.dto.AiUsageSummary;
import work.soho.ai.biz.request.OpenAiChatCompletionRequest;
import work.soho.ai.biz.service.AiApiCallLogService;
import work.soho.ai.biz.service.AiChatService;
import work.soho.ai.biz.service.AiMemberRequestLimitService;
import work.soho.ai.biz.service.AiProviderConfigService;
import work.soho.ai.biz.service.AiProviderModelRelService;
import work.soho.ai.biz.service.AiProxyConfigService;
import work.soho.ai.biz.service.AiProxyRelayService;
import work.soho.ai.biz.service.AiUserApiKeyService;
import work.soho.ai.biz.service.AiUserMemberCardService;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.api.service.WalletInfoApiService;
import work.soho.wallet.biz.service.WalletInfoService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class AiOpenApiServiceImplTest {

    @Test
    public void balance_shouldReturnCodexCompatibleFields() {
        AiUserApiKeyService aiUserApiKeyService = Mockito.mock(AiUserApiKeyService.class);
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiChatService aiChatService = Mockito.mock(AiChatService.class);
        AiApiCallLogService aiApiCallLogService = Mockito.mock(AiApiCallLogService.class);
        WalletInfoService walletInfoService = Mockito.mock(WalletInfoService.class);
        WalletInfoApiService walletInfoApiService = Mockito.mock(WalletInfoApiService.class);
        AiMemberRequestLimitService aiMemberRequestLimitService = Mockito.mock(AiMemberRequestLimitService.class);
        AiUserMemberCardService aiUserMemberCardService = Mockito.mock(AiUserMemberCardService.class);

        AiOpenApiServiceImpl service = new AiOpenApiServiceImpl(
                aiUserApiKeyService,
                aiProviderConfigService,
                aiProviderModelRelService,
                aiChatService,
                Mockito.mock(AiProxyConfigService.class),
                Mockito.mock(AiProxyRelayService.class),
                aiApiCallLogService,
                walletInfoService,
                walletInfoApiService,
                aiMemberRequestLimitService,
                aiUserMemberCardService
        );

        AiUserApiKey apiKey = new AiUserApiKey();
        apiKey.setId(11L);
        apiKey.setUserId(7L);
        when(aiUserApiKeyService.requireByPlaintextKey("token")).thenReturn(apiKey);

        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setId(1L);
        providerConfig.setConfigJson("{\"billingWalletTypeId\":2}");
        when(aiProviderConfigService.list(Mockito.any())).thenReturn(Collections.singletonList(providerConfig));

        WalletInfo walletInfo = new WalletInfo();
        walletInfo.setAmount(new BigDecimal("88.1256"));
        when(walletInfoService.getByUserIdAndType(7L, 2)).thenReturn(walletInfo);

        when(aiUserMemberCardService.resolveActiveMemberCard(7L)).thenReturn(java.util.Optional.empty());
        when(aiApiCallLogService.getMap(Mockito.any()))
                .thenReturn(Map.of(
                        "request_count", 3,
                        "prompt_tokens", 100,
                        "completion_tokens", 50,
                        "total_tokens", 150,
                        "amount", new BigDecimal("1.2500")
                ));

        Map<String, Object> result = service.balance("Bearer token");

        assertThat(result).containsEntry("object", "balance");
        assertThat(result).containsEntry("is_active", true);
        assertThat(result).containsEntry("unit", "USD");
        assertThat(result.get("balance")).isEqualTo(new BigDecimal("88.125600"));
        assertThat(result.get("wallet_type_ids")).isEqualTo(Collections.singletonList(2));
        assertThat(result.get("request_usage")).isInstanceOf(Map.class);
        assertThat(result.get("token_usage")).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> tokenUsage = (Map<String, Object>) result.get("token_usage");
        @SuppressWarnings("unchecked")
        Map<String, Object> todayUsage = (Map<String, Object>) tokenUsage.get("today");
        assertThat(todayUsage.get("amount")).isEqualTo(new BigDecimal("1.250000"));
    }

    @Test
    public void selfPackage_shouldReturnClientCompatibleFields() {
        AiUserApiKeyService aiUserApiKeyService = Mockito.mock(AiUserApiKeyService.class);
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiChatService aiChatService = Mockito.mock(AiChatService.class);
        AiApiCallLogService aiApiCallLogService = Mockito.mock(AiApiCallLogService.class);
        WalletInfoService walletInfoService = Mockito.mock(WalletInfoService.class);
        WalletInfoApiService walletInfoApiService = Mockito.mock(WalletInfoApiService.class);
        AiMemberRequestLimitService aiMemberRequestLimitService = Mockito.mock(AiMemberRequestLimitService.class);
        AiUserMemberCardService aiUserMemberCardService = Mockito.mock(AiUserMemberCardService.class);

        AiOpenApiServiceImpl service = new AiOpenApiServiceImpl(
                aiUserApiKeyService,
                aiProviderConfigService,
                aiProviderModelRelService,
                aiChatService,
                Mockito.mock(AiProxyConfigService.class),
                Mockito.mock(AiProxyRelayService.class),
                aiApiCallLogService,
                walletInfoService,
                walletInfoApiService,
                aiMemberRequestLimitService,
                aiUserMemberCardService
        );

        work.soho.ai.biz.dto.AiUserMemberCardView view = new work.soho.ai.biz.dto.AiUserMemberCardView();
        view.setName("专业版套餐");
        view.setUsageAvailable(true);
        view.setRateLimit7dEnabled(true);
        view.setRateLimit7dUsed(2);
        view.setRateLimit7dRemaining(8);
        view.setLimitMode("by_request");
        when(aiUserMemberCardService.currentUserCard(123L)).thenReturn(java.util.Optional.of(view));

        Map<String, Object> result = service.selfPackage(123L, "123");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.get("data");

        assertThat(result).containsEntry("success", true);
        assertThat(data).containsEntry("group", "专业版套餐");
        assertThat(data).containsEntry("quota", 8L * 500000L);
        assertThat(data).containsEntry("used_quota", 2L * 500000L);
        assertThat(data).containsEntry("total_quota", 10L * 500000L);
    }

    @Test
    public void chatCompletions_whenProviderConfigMissing_throwsClearError() {
        AiUserApiKeyService aiUserApiKeyService = Mockito.mock(AiUserApiKeyService.class);
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiChatService aiChatService = Mockito.mock(AiChatService.class);
        AiApiCallLogService aiApiCallLogService = Mockito.mock(AiApiCallLogService.class);
        WalletInfoService walletInfoService = Mockito.mock(WalletInfoService.class);
        WalletInfoApiService walletInfoApiService = Mockito.mock(WalletInfoApiService.class);
        AiMemberRequestLimitService aiMemberRequestLimitService = Mockito.mock(AiMemberRequestLimitService.class);
        AiUserMemberCardService aiUserMemberCardService = Mockito.mock(AiUserMemberCardService.class);
        when(aiMemberRequestLimitService.evaluate(Mockito.any(), Mockito.any()))
                .thenReturn(AiMemberRequestLimitService.Decision.nonMember());

        AiOpenApiServiceImpl service = new AiOpenApiServiceImpl(
                aiUserApiKeyService,
                aiProviderConfigService,
                aiProviderModelRelService,
                aiChatService,
                Mockito.mock(AiProxyConfigService.class),
                Mockito.mock(AiProxyRelayService.class),
                aiApiCallLogService,
                walletInfoService,
                walletInfoApiService,
                aiMemberRequestLimitService,
                aiUserMemberCardService
        );

        AiUserApiKey apiKey = new AiUserApiKey();
        when(aiUserApiKeyService.requireByPlaintextKey("token")).thenReturn(apiKey);
        when(aiChatService.resolveProviderConfigByProvider("openai", "gpt-4o-mini"))
                .thenThrow(new IllegalArgumentException("provider config not found for model: gpt-4o-mini"));

        OpenAiChatCompletionRequest request = new OpenAiChatCompletionRequest();
        request.setModel("gpt-4o-mini");
        OpenAiChatCompletionRequest.Message message = new OpenAiChatCompletionRequest.Message();
        message.setRole("user");
        message.setContent("hello");
        request.setMessages(java.util.List.of(message));

        assertThatThrownBy(() -> service.chatCompletions("Bearer token", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("provider config not found for model: gpt-4o-mini");
    }

    @Test
    public void chatCompletions_whenProviderConfigResolvedByModel_usesThatConfig() {
        AiUserApiKeyService aiUserApiKeyService = Mockito.mock(AiUserApiKeyService.class);
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiChatService aiChatService = Mockito.mock(AiChatService.class);
        AiApiCallLogService aiApiCallLogService = Mockito.mock(AiApiCallLogService.class);
        WalletInfoService walletInfoService = Mockito.mock(WalletInfoService.class);
        WalletInfoApiService walletInfoApiService = Mockito.mock(WalletInfoApiService.class);
        AiMemberRequestLimitService aiMemberRequestLimitService = Mockito.mock(AiMemberRequestLimitService.class);
        AiUserMemberCardService aiUserMemberCardService = Mockito.mock(AiUserMemberCardService.class);
        when(aiMemberRequestLimitService.evaluate(Mockito.any(), Mockito.any()))
                .thenReturn(AiMemberRequestLimitService.Decision.nonMember());

        AiOpenApiServiceImpl service = new AiOpenApiServiceImpl(
                aiUserApiKeyService,
                aiProviderConfigService,
                aiProviderModelRelService,
                aiChatService,
                Mockito.mock(AiProxyConfigService.class),
                Mockito.mock(AiProxyRelayService.class),
                aiApiCallLogService,
                walletInfoService,
                walletInfoApiService,
                aiMemberRequestLimitService,
                aiUserMemberCardService
        );

        AiUserApiKey apiKey = new AiUserApiKey();
        apiKey.setUserId(7L);
        when(aiUserApiKeyService.requireByPlaintextKey("token")).thenReturn(apiKey);

        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setId(42L);
        providerConfig.setStatus(1);
        providerConfig.setCode("openai-prod");
        providerConfig.setDefaultModel("gpt-4o-mini");
        providerConfig.setProvider("openai");
        providerConfig.setBaseUrl("https://example.com");
        providerConfig.setApiKeyRef("token");
        when(aiChatService.resolveProviderConfigByProvider("openai", "gpt-4o-mini")).thenReturn(providerConfig);

        AiChatResponse response = new AiChatResponse();
        response.setModel("gpt-4o-mini");
        response.setContent("hello");
        response.setPromptTokens(1);
        response.setCompletionTokens(1);
        response.setTotalTokens(2);
        when(aiChatService.chat(Mockito.any())).thenReturn(response);
        AiUsageSummary estimatedUsage = new AiUsageSummary();
        estimatedUsage.setPromptTokens(1);
        estimatedUsage.setCompletionTokens(1);
        estimatedUsage.setTotalTokens(2);
        when(aiChatService.estimateUsage(Mockito.any(), Mockito.anyString())).thenReturn(estimatedUsage);

        OpenAiChatCompletionRequest request = new OpenAiChatCompletionRequest();
        request.setModel("gpt-4o-mini");
        OpenAiChatCompletionRequest.Message message = new OpenAiChatCompletionRequest.Message();
        message.setRole("user");
        message.setContent("hello");
        request.setMessages(java.util.List.of(message));

        service.chatCompletions("Bearer token", request);
        Mockito.verify(aiChatService).resolveProviderConfigByProvider("openai", "gpt-4o-mini");
    }

    @Test
    public void geminiGenerateContent_shouldResolveProviderFromGeminiOnly() {
        AiUserApiKeyService aiUserApiKeyService = Mockito.mock(AiUserApiKeyService.class);
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiChatService aiChatService = Mockito.mock(AiChatService.class);
        AiApiCallLogService aiApiCallLogService = Mockito.mock(AiApiCallLogService.class);
        WalletInfoService walletInfoService = Mockito.mock(WalletInfoService.class);
        WalletInfoApiService walletInfoApiService = Mockito.mock(WalletInfoApiService.class);
        AiMemberRequestLimitService aiMemberRequestLimitService = Mockito.mock(AiMemberRequestLimitService.class);
        AiUserMemberCardService aiUserMemberCardService = Mockito.mock(AiUserMemberCardService.class);

        AiOpenApiServiceImpl service = new AiOpenApiServiceImpl(
                aiUserApiKeyService,
                aiProviderConfigService,
                aiProviderModelRelService,
                aiChatService,
                Mockito.mock(AiProxyConfigService.class),
                Mockito.mock(AiProxyRelayService.class),
                aiApiCallLogService,
                walletInfoService,
                walletInfoApiService,
                aiMemberRequestLimitService,
                aiUserMemberCardService
        );

        AiUserApiKey apiKey = new AiUserApiKey();
        apiKey.setId(10L);
        apiKey.setUserId(7L);
        when(aiUserApiKeyService.requireByPlaintextKey("token")).thenReturn(apiKey);
        when(aiChatService.resolveProviderConfigByProvider("gemini", "gemini-2.5-pro"))
                .thenThrow(new IllegalArgumentException("provider config not found for model: gemini-2.5-pro"));

        assertThatThrownBy(() -> service.geminiGenerateContent("token", "gemini-2.5-pro", Collections.emptyMap()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("provider config not found for model: gemini-2.5-pro");

        Mockito.verify(aiChatService).resolveProviderConfigByProvider("gemini", "gemini-2.5-pro");
    }

    @Test
    public void chatCompletions_whenModelHasPrice_shouldChargeEvenIfProviderBillingDisabled() {
        AiUserApiKeyService aiUserApiKeyService = Mockito.mock(AiUserApiKeyService.class);
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiChatService aiChatService = Mockito.mock(AiChatService.class);
        AiApiCallLogService aiApiCallLogService = Mockito.mock(AiApiCallLogService.class);
        WalletInfoService walletInfoService = Mockito.mock(WalletInfoService.class);
        WalletInfoApiService walletInfoApiService = Mockito.mock(WalletInfoApiService.class);
        AiMemberRequestLimitService aiMemberRequestLimitService = Mockito.mock(AiMemberRequestLimitService.class);
        AiUserMemberCardService aiUserMemberCardService = Mockito.mock(AiUserMemberCardService.class);
        when(aiMemberRequestLimitService.evaluate(Mockito.any(), Mockito.any()))
                .thenReturn(AiMemberRequestLimitService.Decision.nonMember());

        AiOpenApiServiceImpl service = new AiOpenApiServiceImpl(
                aiUserApiKeyService,
                aiProviderConfigService,
                aiProviderModelRelService,
                aiChatService,
                Mockito.mock(AiProxyConfigService.class),
                Mockito.mock(AiProxyRelayService.class),
                aiApiCallLogService,
                walletInfoService,
                walletInfoApiService,
                aiMemberRequestLimitService,
                aiUserMemberCardService
        );

        AiUserApiKey apiKey = new AiUserApiKey();
        apiKey.setId(10L);
        apiKey.setUserId(7L);
        when(aiUserApiKeyService.requireByPlaintextKey("token")).thenReturn(apiKey);

        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setId(42L);
        providerConfig.setCode("openai-prod");
        providerConfig.setProvider("openai");
        providerConfig.setDefaultModel("gpt-4o-mini");
        providerConfig.setConfigJson("{\"billingEnabled\":false,\"billingWalletTypeId\":1}");
        when(aiChatService.resolveProviderConfigByProvider("openai", "gpt-4o-mini")).thenReturn(providerConfig);

        AiModelInfo modelInfo = new AiModelInfo();
        modelInfo.setModelName("gpt-4o-mini");
        modelInfo.setPromptPrice(new BigDecimal("0.5"));
        modelInfo.setCompletionPrice(new BigDecimal("1.0"));
        when(aiProviderModelRelService.listEnabledModelsByProviderConfigId(42L))
                .thenReturn(java.util.List.of(modelInfo));

        WalletInfo walletInfo = new WalletInfo();
        walletInfo.setAmount(new BigDecimal("100.0000"));
        when(walletInfoService.getByUserIdAndType(7L, 1)).thenReturn(walletInfo);

        AiUsageSummary estimatedUsage = new AiUsageSummary();
        estimatedUsage.setPromptTokens(10);
        estimatedUsage.setCompletionTokens(20);
        estimatedUsage.setTotalTokens(30);
        when(aiChatService.estimateUsage(Mockito.any(), Mockito.anyString())).thenReturn(estimatedUsage);

        AiChatResponse response = new AiChatResponse();
        response.setModel("gpt-4o-mini");
        response.setContent("hello");
        response.setPromptTokens(10);
        response.setCompletionTokens(20);
        response.setTotalTokens(30);
        when(aiChatService.chat(Mockito.any())).thenReturn(response);

        when(walletInfoApiService.changeWalletAmount(
                Mockito.eq(7L),
                Mockito.eq(1),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.any(BigDecimal.class),
                Mockito.contains("AI调用扣费")))
                .thenReturn(99L);

        OpenAiChatCompletionRequest request = new OpenAiChatCompletionRequest();
        request.setModel("gpt-4o-mini");
        OpenAiChatCompletionRequest.Message message = new OpenAiChatCompletionRequest.Message();
        message.setRole("user");
        message.setContent("hello");
        request.setMessages(java.util.List.of(message));

        service.chatCompletions("Bearer token", request);

        Mockito.verify(walletInfoApiService).changeWalletAmount(
                Mockito.eq(7L),
                Mockito.eq(1),
                Mockito.anyInt(),
                Mockito.anyString(),
                Mockito.argThat(amount -> amount != null && amount.compareTo(new BigDecimal("-0.500000")) == 0),
                Mockito.argThat(notes ->
                        notes != null
                                && notes.contains("AI调用扣费 model=gpt-4o-mini")
                                && notes.contains("inputTokens=10")
                                && notes.contains("outputTokens=20")
                                && notes.contains("totalTokens=30"))
        );
    }

    @Test
    public void chatCompletions_whenWalletMissing_shouldThrowWalletNotFound() {
        AiUserApiKeyService aiUserApiKeyService = Mockito.mock(AiUserApiKeyService.class);
        AiProviderConfigService aiProviderConfigService = Mockito.mock(AiProviderConfigService.class);
        AiProviderModelRelService aiProviderModelRelService = Mockito.mock(AiProviderModelRelService.class);
        AiChatService aiChatService = Mockito.mock(AiChatService.class);
        AiApiCallLogService aiApiCallLogService = Mockito.mock(AiApiCallLogService.class);
        WalletInfoService walletInfoService = Mockito.mock(WalletInfoService.class);
        WalletInfoApiService walletInfoApiService = Mockito.mock(WalletInfoApiService.class);
        AiMemberRequestLimitService aiMemberRequestLimitService = Mockito.mock(AiMemberRequestLimitService.class);
        AiUserMemberCardService aiUserMemberCardService = Mockito.mock(AiUserMemberCardService.class);
        when(aiMemberRequestLimitService.evaluate(Mockito.any(), Mockito.any()))
                .thenReturn(AiMemberRequestLimitService.Decision.nonMember());

        AiOpenApiServiceImpl service = new AiOpenApiServiceImpl(
                aiUserApiKeyService,
                aiProviderConfigService,
                aiProviderModelRelService,
                aiChatService,
                Mockito.mock(AiProxyConfigService.class),
                Mockito.mock(AiProxyRelayService.class),
                aiApiCallLogService,
                walletInfoService,
                walletInfoApiService,
                aiMemberRequestLimitService,
                aiUserMemberCardService
        );

        AiUserApiKey apiKey = new AiUserApiKey();
        apiKey.setId(10L);
        apiKey.setUserId(7L);
        when(aiUserApiKeyService.requireByPlaintextKey("token")).thenReturn(apiKey);

        AiProviderConfig providerConfig = new AiProviderConfig();
        providerConfig.setId(42L);
        providerConfig.setCode("openai-prod");
        providerConfig.setProvider("openai");
        providerConfig.setDefaultModel("gpt-4o-mini");
        providerConfig.setConfigJson("{\"billingEnabled\":true,\"billingWalletTypeId\":1,\"promptPricePer1kTokens\":1}");
        when(aiChatService.resolveProviderConfigByProvider("openai", "gpt-4o-mini")).thenReturn(providerConfig);
        when(aiProviderModelRelService.listEnabledModelsByProviderConfigId(42L))
                .thenReturn(java.util.List.of());

        AiUsageSummary estimatedUsage = new AiUsageSummary();
        estimatedUsage.setPromptTokens(10);
        estimatedUsage.setCompletionTokens(0);
        estimatedUsage.setTotalTokens(10);
        when(aiChatService.estimateUsage(Mockito.any(), Mockito.anyString())).thenReturn(estimatedUsage);
        when(walletInfoService.getByUserIdAndType(7L, 1)).thenReturn(null);

        OpenAiChatCompletionRequest request = new OpenAiChatCompletionRequest();
        request.setModel("gpt-4o-mini");
        OpenAiChatCompletionRequest.Message message = new OpenAiChatCompletionRequest.Message();
        message.setRole("user");
        message.setContent("hello");
        request.setMessages(java.util.List.of(message));

        assertThatThrownBy(() -> service.chatCompletions("Bearer token", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("钱包不存在");
    }
}
