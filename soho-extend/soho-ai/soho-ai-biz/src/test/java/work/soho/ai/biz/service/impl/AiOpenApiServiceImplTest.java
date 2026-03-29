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
import work.soho.ai.biz.service.AiUserApiKeyService;
import work.soho.ai.biz.service.AiUserMemberCardService;
import work.soho.wallet.biz.domain.WalletInfo;
import work.soho.wallet.api.service.WalletInfoApiService;
import work.soho.wallet.biz.service.WalletInfoService;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class AiOpenApiServiceImplTest {

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
                aiApiCallLogService,
                walletInfoService,
                walletInfoApiService,
                aiMemberRequestLimitService,
                aiUserMemberCardService
        );

        AiUserApiKey apiKey = new AiUserApiKey();
        when(aiUserApiKeyService.requireByPlaintextKey("token")).thenReturn(apiKey);
        when(aiChatService.resolveProviderConfig(null, "gpt-4o-mini"))
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
        when(aiChatService.resolveProviderConfig(null, "gpt-4o-mini")).thenReturn(providerConfig);

        AiChatResponse response = new AiChatResponse();
        response.setModel("gpt-4o-mini");
        response.setContent("hello");
        response.setPromptTokens(1);
        response.setCompletionTokens(1);
        response.setTotalTokens(2);
        when(aiChatService.chat(Mockito.eq(providerConfig), Mockito.any())).thenReturn(response);
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
        Mockito.verify(aiChatService).resolveProviderConfig(null, "gpt-4o-mini");
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
        providerConfig.setDefaultModel("gpt-4o-mini");
        providerConfig.setConfigJson("{\"billingEnabled\":false,\"billingWalletTypeId\":1}");
        when(aiChatService.resolveProviderConfig(null, "gpt-4o-mini")).thenReturn(providerConfig);

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
        when(aiChatService.chat(Mockito.eq(providerConfig), Mockito.any())).thenReturn(response);

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
                Mockito.eq(new BigDecimal("-0.0250")),
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
        providerConfig.setDefaultModel("gpt-4o-mini");
        providerConfig.setConfigJson("{\"billingEnabled\":true,\"billingWalletTypeId\":1,\"promptPricePer1kTokens\":1}");
        when(aiChatService.resolveProviderConfig(null, "gpt-4o-mini")).thenReturn(providerConfig);
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
